package it.albertus.eqbulletin.service.geofon;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.BulletinProvider;
import it.albertus.eqbulletin.service.CancelException;
import it.albertus.eqbulletin.service.DecodeException;
import it.albertus.eqbulletin.service.FetchException;
import it.albertus.eqbulletin.service.SearchJobVars;
import it.albertus.eqbulletin.service.geofon.html.transformer.HtmlElementTransformer;
import it.albertus.eqbulletin.service.geofon.rss.transformer.RssItemTransformer;
import it.albertus.eqbulletin.service.geofon.rss.xml.Rss;
import it.albertus.eqbulletin.service.net.HttpConnector;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;

public class GeofonBulletinProvider implements BulletinProvider {

	public static final String DEFAULT_BASE_URL = "http://geofon.gfz-potsdam.de";

	private static final Logger logger = LoggerFactory.getLogger(GeofonBulletinProvider.class);

	private static final Configuration configuration = EarthquakeBulletinConfig.getInstance();

	private HttpURLConnection urlConnection;

	private boolean cancelled;

	@Override
	public List<Earthquake> getEarthquakes(final SearchJobVars jobVariables) throws FetchException, DecodeException {
		final String url = getUrl(jobVariables.getParams());

		Rss rss = null;
		Document html = null;

		try {
			synchronized (this) {
				if (cancelled) {
					throw new CancelException();
				}
				else {
					urlConnection = getConnection(url);
				}
			}
			final String responseContentEncoding = urlConnection.getContentEncoding(); // Connection starts here
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			try (final InputStream internalInputStream = urlConnection.getInputStream(); final InputStream inputStream = gzip ? new GZIPInputStream(internalInputStream) : internalInputStream) {
				if (Format.RSS.equals(jobVariables.getFormat())) {
					rss = fetchRss(inputStream);
				}
				else if (Format.HTML.equals(jobVariables.getFormat())) {
					html = fetchHtml(inputStream, url);
				}
				else {
					throw new UnsupportedOperationException(String.valueOf(jobVariables.getFormat()));
				}
			}
		}
		catch (final Exception e) {
			throw new FetchException(Messages.get("err.job.fetch"), e);
		}
		finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		// Decode
		try {
			if (rss != null) {
				return RssItemTransformer.fromRss(rss);
			}
			else if (html != null) {
				return HtmlElementTransformer.fromHtml(html);
			}
			else {
				throw new IllegalStateException();
			}
		}
		catch (final Exception e) {
			throw new DecodeException(Messages.get("err.job.decode"), e);
		}
	}

	@Override
	public synchronized void cancel() {
		cancelled = true;
		if (urlConnection != null) {
			try {
				urlConnection.getInputStream().close(); // Interrupt blocking I/O
			}
			catch (final Exception e) {
				logger.log(Level.FINE, e.toString(), e);
			}
		}
	}

	private static String getUrl(final Map<String, String> params) {
		final StringBuilder url = new StringBuilder(configuration.getString("url.base", DEFAULT_BASE_URL)).append("/eqinfo/list.php?fmt=").append(params.get("fmt"));
		for (final Entry<String, String> param : params.entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !"fmt".equals(param.getKey())) {
				url.append('&').append(param.getKey()).append('=').append(param.getValue());
			}
		}
		return url.toString();
	}

	private static HttpURLConnection getConnection(final String url) throws IOException {
		final HttpURLConnection conn = HttpConnector.getConnection(url);
		conn.setRequestProperty("Accept", "*/html,*/xml,*/*;q=0.9");
		conn.setRequestProperty("Accept-Encoding", "gzip");
		return conn;
	}

	private static Rss fetchRss(final InputStream is) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (Rss) jaxbUnmarshaller.unmarshal(is);
	}

	private static Document fetchHtml(final InputStream is, final String url) throws IOException {
		return Jsoup.parse(is, null, url);
	}

}
