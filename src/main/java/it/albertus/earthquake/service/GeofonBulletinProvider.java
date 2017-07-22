package it.albertus.earthquake.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.Format;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.service.html.TableData;
import it.albertus.earthquake.service.html.transformer.HtmlTableDataTransformer;
import it.albertus.earthquake.service.net.HttpConnector;
import it.albertus.earthquake.service.rss.transformer.RssItemTransformer;
import it.albertus.earthquake.service.rss.xml.Rss;
import it.albertus.util.IOUtils;
import it.albertus.util.NewLine;

public class GeofonBulletinProvider implements BulletinProvider {

	@Override
	public Collection<Earthquake> getEarthquakes(final SearchJobVars jobVariables) throws FetchException, DecodeException {
		final StringBuilder url = new StringBuilder(EarthquakeBulletin.BASE_URL).append("/eqinfo/list.php?fmt=").append(jobVariables.getParams().get("fmt"));
		for (final Entry<String, String> param : jobVariables.getParams().entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !"fmt".equals(param.getKey())) {
				url.append('&').append(param.getKey()).append('=').append(param.getValue());
			}
		}

		Rss rss = null;
		TableData html = null;
		InputStream innerStream = null;
		InputStream wrapperStream = null;
		// Fetch
		try {
			final HttpURLConnection urlConnection = openConnection(url.toString());
			final String responseContentEncoding = urlConnection.getContentEncoding(); // Connection starts here
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			innerStream = urlConnection.getInputStream();
			if (gzip) {
				wrapperStream = new GZIPInputStream(innerStream);
			}
			else {
				wrapperStream = innerStream;
			}

			if (Format.RSS.equals(jobVariables.getFormat())) {
				rss = fetchRss(wrapperStream);
				urlConnection.disconnect();
			}
			else if (Format.HTML.equals(jobVariables.getFormat())) {
				html = fetchHtml(wrapperStream);
			}
			else {
				throw new UnsupportedOperationException(String.valueOf(jobVariables.getFormat()));
			}
		}
		catch (final Exception e) {
			throw new FetchException(Messages.get("err.job.fetch"), e);
		}
		finally {
			IOUtils.closeQuietly(wrapperStream, innerStream);
		}

		// Decode
		final Collection<Earthquake> earthquakes = new TreeSet<>();
		try {
			if (Format.RSS.equals(jobVariables.getFormat())) {
				earthquakes.addAll(RssItemTransformer.fromRss(rss));
			}
			else if (Format.HTML.equals(jobVariables.getFormat())) {
				earthquakes.addAll(HtmlTableDataTransformer.fromHtml(html));
			}
			else {
				throw new UnsupportedOperationException(String.valueOf(jobVariables.getFormat()));
			}
		}
		catch (final Exception e) {
			throw new DecodeException(Messages.get("err.job.decode"), e);
		}

		return earthquakes;
	}

	private Rss fetchRss(final InputStream is) throws JAXBException {
		final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		return (Rss) jaxbUnmarshaller.unmarshal(is);
	}

	private TableData fetchHtml(final InputStream is) throws IOException {
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

	private HttpURLConnection openConnection(final String url) throws IOException {
		final HttpURLConnection urlConnection = HttpConnector.openConnection(url);
		urlConnection.addRequestProperty("Accept", "*/html,*/xml");
		urlConnection.addRequestProperty("Accept-Encoding", "gzip");
		return urlConnection;
	}

}
