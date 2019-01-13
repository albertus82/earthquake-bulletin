package it.albertus.eqbulletin.service.net;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.CancelException;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.decode.html.HtmlBulletin;
import it.albertus.eqbulletin.service.decode.html.HtmlBulletinDecoder;
import it.albertus.util.NewLine;
import it.albertus.util.logging.LoggerFactory;

public class HtmlBulletinDownloader implements BulletinDownloader {

	private static final Logger logger = LoggerFactory.getLogger(HtmlBulletinDownloader.class);

	private InputStream connectionInputStream;

	@Override
	public Collection<Earthquake> download(final SearchRequest request, final BooleanSupplier canceled) throws IOException, DecodeException, CancelException {
		final Headers headers = new Headers();
		headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.set("Accept-Encoding", "gzip");
		if (canceled.getAsBoolean()) {
			logger.fine("Download canceled before connection.");
			throw new CancelException();
		}
		final HttpURLConnection connection = ConnectionFactory.makeGetRequest(request.getUrl(), headers);
		return parseResponseContent(connection, canceled);
	}

	private Collection<Earthquake> parseResponseContent(final URLConnection connection, final BooleanSupplier canceled) throws IOException, DecodeException, CancelException {
		final String responseContentEncoding = connection.getContentEncoding();
		final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
		try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw; final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			if (canceled.getAsBoolean()) {
				logger.fine("Download canceled after connection.");
				throw new CancelException();
			}
			connectionInputStream = raw;
			final Charset charset = ConnectionUtils.detectCharset(connection);
			final HtmlBulletin fetched = fetch(in, charset);
			return decode(fetched);
		}
		catch (final IOException e) {
			if (canceled.getAsBoolean()) {
				logger.log(Level.FINE, "Download canceled during download:", e);
				throw new CancelException();
			}
			else {
				throw e;
			}
		}
	}

	private static HtmlBulletin fetch(final InputStream in, final Charset charset) throws IOException {
		try {
			final HtmlBulletin td = new HtmlBulletin();
			try (final InputStreamReader isr = new InputStreamReader(in, charset); final BufferedReader br = new BufferedReader(isr)) {
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
		catch (final Exception e) {
			throw new FetchException(Messages.get("err.job.fetch"), e);
		}
	}

	private static Collection<Earthquake> decode(final HtmlBulletin tableData) throws DecodeException {
		try {
			return HtmlBulletinDecoder.decode(tableData);
		}
		catch (final Exception e) {
			throw new DecodeException(Messages.get("err.job.decode"), e);
		}
	}

	@Override
	public void cancel() {
		if (connectionInputStream != null) {
			try {
				connectionInputStream.close();
				logger.fine("Download canceled.");
			}
			catch (final Exception e) {
				logger.log(Level.FINE, e.toString(), e);
			}
		}
	}

}
