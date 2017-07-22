package it.albertus.earthquake.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
		final Collection<Earthquake> earthquakes = new TreeSet<>();

		final StringBuilder urlSb = new StringBuilder(EarthquakeBulletin.BASE_URL).append("/eqinfo/list.php?fmt=").append(jobVariables.getParams().get("fmt"));
		for (final Entry<String, String> param : jobVariables.getParams().entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !"fmt".equals(param.getKey())) {
				urlSb.append("&").append(param.getKey()).append("=").append(param.getValue());
			}
		}

		Rss rss = null;
		TableData html = null;
		InputStream innerStream = null;
		InputStream wrapperStream = null;
		try {
			// Download
			try {
				final URL url = new URL(urlSb.toString());
				final HttpURLConnection urlConnection = openConnection(url);
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
					rss = downloadRss(wrapperStream);
					urlConnection.disconnect();
				}
				else if (Format.HTML.equals(jobVariables.getFormat())) {
					html = downloadHtml(wrapperStream);
				}
				else {
					throw new UnsupportedOperationException(String.valueOf(jobVariables.getFormat()));
				}
			}
			catch (final Exception e) {
				throw new FetchException(Messages.get("err.job.search"), e);
			}

			// Decode
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
		}
		finally {
			IOUtils.closeQuietly(wrapperStream, innerStream);
		}
		return earthquakes;
	}

	private Rss downloadRss(final InputStream wrapperStream) throws JAXBException {
		Rss rss;
		final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		rss = (Rss) jaxbUnmarshaller.unmarshal(wrapperStream);
		return rss;
	}

	private TableData downloadHtml(final InputStream wrapperStream) throws IOException {
		TableData td;
		td = new TableData();
		try (final BufferedReader br = new BufferedReader(new InputStreamReader(wrapperStream))) {
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

	private HttpURLConnection openConnection(final URL url) throws IOException {
		final HttpURLConnection urlConnection = HttpConnector.openConnection(url);
		urlConnection.addRequestProperty("Accept", "*/html,*/xml");
		urlConnection.addRequestProperty("Accept-Encoding", "gzip");
		return urlConnection;
	}

}
