package it.albertus.eqbulletin.service.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import io.github.resilience4j.decorators.Decorators;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class StaticResourceDownloader<T extends StaticResource> extends ResilientDownloader {

	private static final String DEFAULT_ACCEPT = "*/*";

	private final String accept;

	protected StaticResourceDownloader() {
		this(DEFAULT_ACCEPT);
	}

	protected abstract T makeObject(InputStream in, URLConnection connection) throws IOException;

	protected T doDownload(@NonNull final URL url) throws IOException {
		return doDownload(url, null, () -> false);
	}

	protected T doDownload(@NonNull final URL url, final BooleanSupplier canceled) throws IOException {
		return doDownload(url, null, canceled);
	}

	protected T doDownload(@NonNull final URL url, final T cached) throws IOException {
		return doDownload(url, cached, () -> false);
	}

	@SneakyThrows
	protected T doDownload(@NonNull final URL url, final T cached, final BooleanSupplier canceled) throws IOException {
		final Headers headers = new Headers();
		headers.set("Accept", accept);
		headers.set("Accept-Encoding", "gzip");
		if (cached != null && cached.getEtag() != null && !cached.getEtag().trim().isEmpty()) {
			headers.set("If-None-Match", cached.getEtag());
		}
		if (canceled != null && canceled.getAsBoolean()) {
			log.fine("Download canceled before connection.");
			return null;
		}
		return Decorators.ofCheckedSupplier(() -> fetch(url, cached, canceled, headers)).withCircuitBreaker(circuitBreaker).withRetry(retry).get();
	}

	private T fetch(@NonNull final URL url, final T cached, final BooleanSupplier canceled, final Headers headers) throws IOException {
		final HttpURLConnection connection = ConnectionFactory.makeGetRequest(url, headers);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
			return cached; // Not modified.
		}
		else {
			return parseResponseContent(connection, cached, canceled);
		}
	}

	private T parseResponseContent(@NonNull final URLConnection connection, final T cached, final BooleanSupplier canceled) throws IOException {
		final String responseContentEncoding = connection.getContentEncoding();
		final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase(Locale.ROOT).contains("gzip");
		try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw) {
			connectionInputStream = raw;
			if (canceled != null && canceled.getAsBoolean()) {
				log.fine("Download canceled after connection.");
				return null;
			}
			final T downloaded = makeObject(in, connection);
			if (downloaded.equals(cached)) {
				log.fine("downloaded.equals(cached)");
				return cached;
			}
			else {
				return downloaded;
			}
		}
		catch (final IOException e) {
			if (canceled != null && canceled.getAsBoolean()) {
				log.log(Level.FINE, "Download canceled during download:", e);
				return null;
			}
			else {
				throw e;
			}
		}
	}

}
