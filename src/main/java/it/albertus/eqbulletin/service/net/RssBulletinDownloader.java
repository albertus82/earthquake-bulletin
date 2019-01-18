package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.decode.rss.RssBulletinDecoder;
import it.albertus.eqbulletin.service.decode.rss.xml.RssBulletin;
import it.albertus.eqbulletin.util.InitializationException;
import it.albertus.util.IOUtils;
import it.albertus.util.logging.LoggerFactory;

public class RssBulletinDownloader implements BulletinDownloader {

	private static final Logger logger = LoggerFactory.getLogger(RssBulletinDownloader.class);

	private static final short BUFFER_SIZE = 4096;

	private static final String CANCELED_MESSAGE = "Operation canceled";

	private static final JAXBContext jaxbContext;

	static {
		try {
			jaxbContext = JAXBContext.newInstance(RssBulletin.class);
		}
		catch (final JAXBException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new InitializationException(e.toString(), e);
		}
	}

	private InputStream connectionInputStream;

	@Override
	public Collection<Earthquake> download(final SearchRequest request, final BooleanSupplier canceled) throws FetchException, DecodeException, InterruptedException {
		final Headers headers = new Headers();
		headers.set("Accept", "text/xml,*/xml;q=0.9,*/*;q=0.8");
		headers.set("Accept-Encoding", "gzip");
		if (canceled.getAsBoolean()) {
			throw new InterruptedException("Download canceled before connection.");
		}
		try {
			return download(request, headers, canceled);
		}
		catch (final FetchException | DecodeException | RuntimeException e) {
			if (canceled.getAsBoolean()) {
				logger.log(Level.FINE, CANCELED_MESSAGE + ':', e);
				throw new InterruptedException(CANCELED_MESSAGE + '.');
			}
			else {
				throw e;
			}
		}
	}

	private Collection<Earthquake> download(final SearchRequest request, final Headers headers, final BooleanSupplier canceled) throws FetchException, DecodeException, InterruptedException {
		final String body;
		try {
			final URLConnection connection = ConnectionFactory.makeGetRequest(request.toURL(), headers);
			final String responseContentEncoding = connection.getContentEncoding();
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw; final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				connectionInputStream = raw;
				if (canceled.getAsBoolean()) {
					throw new InterruptedException(CANCELED_MESSAGE + '.');
				}
				final Charset charset = ConnectionUtils.detectCharset(connection);
				body = fetch(in, charset);
			}
		}
		catch (final IOException | RuntimeException e) {
			throw new FetchException(Messages.get("err.job.fetch"), e);
		}
		try {
			if (canceled.getAsBoolean()) {
				throw new InterruptedException(CANCELED_MESSAGE + '.');
			}
			return decode(body);
		}
		catch (final JAXBException | RuntimeException e) {
			throw new DecodeException(Messages.get("err.job.decode"), e);
		}
	}

	private static String fetch(final InputStream in, final Charset charset) throws IOException {
		try (final InputStreamReader isr = new InputStreamReader(in, charset); final StringWriter sw = new StringWriter()) {
			IOUtils.copy(isr, sw, BUFFER_SIZE);
			return sw.toString().replace("geofon:mt", "geofon_mt");
		}
	}

	private static Collection<Earthquake> decode(final String body) throws JAXBException {
		final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		try (final StringReader sr = new StringReader(body)) {
			return RssBulletinDecoder.decode((RssBulletin) jaxbUnmarshaller.unmarshal(sr));
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
