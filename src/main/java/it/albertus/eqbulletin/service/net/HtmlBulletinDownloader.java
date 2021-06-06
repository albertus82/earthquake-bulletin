package it.albertus.eqbulletin.service.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.sun.net.httpserver.Headers;

import io.github.resilience4j.decorators.Decorators;
import it.albertus.eqbulletin.model.Bulletin;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.decode.html.HtmlBulletinDecoder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public class HtmlBulletinDownloader extends ResilientDownloader implements BulletinDownloader {

	private final HtmlBulletinDecoder decoder;

	@Override
	public Optional<Bulletin> download(@NonNull final SearchRequest request, final BooleanSupplier canceled) throws FetchException, DecodeException {
		try {
			return Optional.of(new Bulletin(doDownload(request, canceled)));
		}
		catch (final CancelException e) {
			log.log(Level.FINE, "Operation canceled:", e);
			return Optional.empty();
		}
	}

	private Collection<Earthquake> doDownload(@NonNull final SearchRequest request, final BooleanSupplier canceled) throws FetchException, DecodeException, CancelException {
		final Headers headers = new Headers();
		headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.set("Accept-Encoding", "gzip");
		if (canceled != null && canceled.getAsBoolean()) {
			throw new CancelException("Download canceled before connection.");
		}
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

	private List<Earthquake> download(@NonNull final SearchRequest request, final Headers headers, final BooleanSupplier canceled) throws FetchException, DecodeException, CancelException {
		try {
			final List<Earthquake> result = new ArrayList<>();
			final Collection<URI> uris = request.toURIs();
			final Optional<Short> limit = request.getLimit();
			for (final URI uri : uris) {
				final Collection<Earthquake> partial = downloadPage(uri, headers, canceled);
				log.log(Level.FINE, "partial.size() = {0,number,#}", partial.size());
				if (partial.isEmpty()) {
					break;
				}
				result.addAll(partial);
				if (limit.isPresent() && partial.size() < limit.get() / uris.size()) {
					break;
				}
			}
			if (limit.isPresent() && result.size() > limit.get()) {
				return result.subList(0, limit.get());
			}
			else {
				return result;
			}
		}
		catch (final URISyntaxException e) {
			throw new FetchException(Messages.get("error.job.fetch"), e);
		}
	}

	@SneakyThrows
	private List<Earthquake> downloadPage(final URI uri, final Headers headers, final BooleanSupplier canceled) throws CancelException, FetchException, DecodeException {
		final Document document;
		try {
			document = Decorators.ofCheckedSupplier(() -> fetch(uri, headers, canceled)).withCircuitBreaker(circuitBreaker).withRetry(retry).get();
		}
		catch (final IOException | RuntimeException e) {
			throw new FetchException(Messages.get("error.job.fetch"), e);
		}
		try {
			if (canceled != null && canceled.getAsBoolean()) {
				throw new CancelException();
			}
			return decoder.decode(document);
		}
		catch (final RuntimeException e) {
			throw new DecodeException(Messages.get("error.job.decode"), e);
		}
	}

	private Document fetch(@NonNull final URI uri, final Headers headers, final BooleanSupplier canceled) throws IOException, CancelException {
		if (canceled != null && canceled.getAsBoolean()) {
			throw new CancelException();
		}
		final URLConnection connection = ConnectionFactory.makeGetRequest(uri.toURL(), headers);
		final String responseContentEncoding = connection.getContentEncoding();
		final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase(Locale.ROOT).contains("gzip");
		try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw) {
			connectionInputStream = raw;
			if (canceled != null && canceled.getAsBoolean()) {
				throw new CancelException();
			}
			final Charset charset = ConnectionUtils.detectCharset(connection);
			return fetch(in, charset, uri);
		}
	}

	private static Document fetch(final InputStream in, final Charset charset, final URI uri) throws IOException {
		return Jsoup.parse(in, charset.name(), uri.getPath().endsWith("/") ? uri.resolve("..").toString() : uri.resolve(".").toString());
	}

}
