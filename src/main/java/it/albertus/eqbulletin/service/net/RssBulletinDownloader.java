package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.CancelException;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.decode.rss.RssBulletinDecoder;
import it.albertus.eqbulletin.service.decode.rss.xml.RssBulletin;
import it.albertus.util.IOUtils;
import it.albertus.util.logging.LoggerFactory;

public class RssBulletinDownloader implements BulletinDownloader {

	private static final Logger logger = LoggerFactory.getLogger(RssBulletinDownloader.class);

	private static final short BUFFER_SIZE = 4096;

	private InputStream connectionInputStream;

	@Override
	public Collection<Earthquake> download(final SearchRequest request, final BooleanSupplier canceled) throws IOException, DecodeException, CancelException {
		final Headers headers = new Headers();
		headers.set("Accept", "text/xml,*/xml;q=0.9,*/*;q=0.8");
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
			connectionInputStream = raw;
			if (canceled.getAsBoolean()) {
				logger.fine("Download canceled after connection.");
				throw new CancelException();
			}
			final Charset charset = ConnectionUtils.detectCharset(connection);
			final RssBulletin fetched = fetch(in, charset);
			return decode(fetched);
		}
		catch (final IOException e) {
			if (canceled.getAsBoolean()) {
				logger.log(Level.FINE, "Download canceled:", e);
				throw new CancelException();
			}
			else {
				throw e;
			}
		}
	}

	private static RssBulletin fetch(final InputStream in, final Charset charset) throws IOException {
		try {
			final String body;
			try (final InputStreamReader isr = new InputStreamReader(in, charset); final StringWriter sw = new StringWriter()) {
				IOUtils.copy(isr, sw, BUFFER_SIZE);
				body = sw.toString().replace("geofon:mt", "geofon_mt");
			}

			final JAXBContext jaxbContext = JAXBContext.newInstance(RssBulletin.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			try (final StringReader sr = new StringReader(body)) {
				return (RssBulletin) jaxbUnmarshaller.unmarshal(sr);
			}
		}
		catch (final Exception e) {
			throw new FetchException(Messages.get("err.job.fetch"), e);
		}
	}

	private static Collection<Earthquake> decode(final RssBulletin rss) throws DecodeException {
		try {
			return RssBulletinDecoder.decode(rss);
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
			}
			catch (final Exception e) {
				logger.log(Level.FINE, e.toString(), e);
			}
		}
	}

}
