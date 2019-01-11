package it.albertus.eqbulletin.service.geofon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.BulletinProvider;
import it.albertus.eqbulletin.service.CancelException;
import it.albertus.eqbulletin.service.DecodeException;
import it.albertus.eqbulletin.service.FetchException;
import it.albertus.eqbulletin.service.SearchJobVars;
import it.albertus.eqbulletin.service.geofon.html.TableData;
import it.albertus.eqbulletin.service.geofon.html.transformer.HtmlTableDataTransformer;
import it.albertus.eqbulletin.service.geofon.rss.transformer.RssItemTransformer;
import it.albertus.eqbulletin.service.geofon.rss.xml.Rss;
import it.albertus.eqbulletin.service.net.ConnectionFactory;
import it.albertus.util.NewLine;
import it.albertus.util.config.IConfiguration;
import it.albertus.util.logging.LoggerFactory;

public class GeofonBulletinProvider implements BulletinProvider {

	public static final String DEFAULT_BASE_URL = "https://geofon.gfz-potsdam.de";

	private static final Logger logger = LoggerFactory.getLogger(GeofonBulletinProvider.class);

	private static final IConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private HttpURLConnection urlConnection;

	@Override
	public List<Earthquake> getEarthquakes(final SearchJobVars jobVariables, final BooleanSupplier canceled) throws FetchException, DecodeException {
		final String url = getUrl(jobVariables.getParams());

		Rss rss = null;
		TableData html = null;

		try {
			synchronized (this) {
				if (canceled.getAsBoolean()) {
					throw new CancelException();
				}
				else {
					urlConnection = prepareConnection(url);
				}
			}
			final String responseContentEncoding = urlConnection.getContentEncoding(); // Connection starts here
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			try (final InputStream internalInputStream = urlConnection.getInputStream(); final InputStream inputStream = gzip ? new GZIPInputStream(internalInputStream) : internalInputStream) {
				if (Format.RSS.equals(jobVariables.getFormat())) {
					rss = fetchRss(inputStream);
				}
				else if (Format.HTML.equals(jobVariables.getFormat())) {
					html = fetchHtml(inputStream);
				}
				else {
					throw new UnsupportedOperationException(String.valueOf(jobVariables.getFormat()));
				}
			}
		}
		catch (final Exception e) {
			throw new FetchException(Messages.get("err.job.fetch"), e);
		}

		// Decode
		try {
			if (rss != null) {
				return RssItemTransformer.fromRss(rss);
			}
			else if (html != null) {
				return HtmlTableDataTransformer.fromHtml(html);
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

	private static HttpURLConnection prepareConnection(final String url) throws IOException {
		final Headers headers = new Headers();
		headers.set("Accept", "*/html,*/xml,*/*;q=0.9");
		headers.set("Accept-Encoding", "gzip");
		return ConnectionFactory.prepareConnection(new URL(url), headers);
	}

	private static Rss fetchRss(final InputStream is) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (Rss) jaxbUnmarshaller.unmarshal(is);
	}

	private static TableData fetchHtml(final InputStream is) throws IOException {
		final TableData td = new TableData();
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.trim().toLowerCase().contains("<tr")) {
					final StringBuilder block = new StringBuilder();
					while (!(line = br.readLine()).toLowerCase().contains("</tr")) {
						block.append(line.trim()).append(NewLine.SYSTEM_LINE_SEPARATOR);
					}
					td.addItem(block.toString());
				}
			}
		}
		return td;
	}

}
