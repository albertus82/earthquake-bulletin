package io.github.albertus82.eqbulletin.service.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import io.github.albertus82.eqbulletin.model.Bulletin;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.eqbulletin.service.SearchRequest;
import io.github.albertus82.eqbulletin.service.decode.DecodeException;
import io.github.albertus82.eqbulletin.service.decode.rss.RssBulletinDecoder;
import io.github.albertus82.eqbulletin.service.decode.rss.xml.RssBulletin;
import io.github.albertus82.util.IOUtils;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RssBulletinDownloader extends ResilientDownloader implements BulletinDownloader {

	private static final short BUFFER_SIZE = 4096;

	private static final JAXBContext jaxbContext;

	static {
		try {
			jaxbContext = JAXBContext.newInstance(RssBulletin.class);
		}
		catch (final JAXBException e) {
			throw new IllegalStateException("Cannot create instance of " + JAXBContext.class + " for " + RssBulletin.class, e);
		}
	}

	@Override
	public Optional<Bulletin> download(@NonNull final SearchRequest request, final BooleanSupplier canceled) throws FetchException, DecodeException {
		try {
			return Optional.of(new Bulletin(doDownload(request, canceled)));
		}
		catch (final CancelException e) {
			log.debug("Operation canceled:", e);
			return Optional.empty();
		}
	}

	private Collection<Earthquake> doDownload(@NonNull final SearchRequest request, final BooleanSupplier canceled) throws FetchException, DecodeException, CancelException {
		final Headers headers = new Headers();
		headers.set("Accept", "text/xml,*/xml;q=0.9,*/*;q=0.8");
		headers.set("Accept-Encoding", "gzip");
		try {
			return download(request, headers, canceled);
		}
		catch (final FetchException | DecodeException | RuntimeException e) {
			if (canceled != null && canceled.getAsBoolean()) {
				throw new CancelException(e);
			}
			else {
				throw e;
			}
		}
	}

	@SneakyThrows
	private Collection<Earthquake> download(@NonNull final SearchRequest request, final Headers headers, final BooleanSupplier canceled) throws FetchException, DecodeException, CancelException {
		final String body;
		try {
			body = newResilientSupplier(() -> fetch(request, headers, canceled)).get();
		}
		catch (final IOException | RuntimeException | URISyntaxException e) {
			throw new FetchException("Cannot download the earthquake bulletin", e);
		}
		try {
			if (canceled != null && canceled.getAsBoolean()) {
				throw new CancelException();
			}
			return decode(body);
		}
		catch (final JAXBException | RuntimeException e) {
			throw new DecodeException("Cannot decode the earthquake bulletin", e);
		}
	}

	private String fetch(@NonNull final SearchRequest request, final Headers headers, final BooleanSupplier canceled) throws IOException, URISyntaxException, CancelException {
		if (canceled != null && canceled.getAsBoolean()) {
			throw new CancelException();
		}
		final URLConnection connection = ConnectionFactory.makeGetRequest(request.toURIs().get(0).toURL(), headers);
		final String responseContentEncoding = connection.getContentEncoding();
		final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase(Locale.ROOT).contains("gzip");
		try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw) {
			connectionInputStream = raw;
			if (canceled != null && canceled.getAsBoolean()) {
				throw new CancelException();
			}
			final Charset charset = ConnectionUtils.detectCharset(connection);
			return fetch(in, charset);
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

}
