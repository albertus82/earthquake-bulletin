package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.function.BooleanSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.util.IOUtils;
import it.albertus.util.logging.LoggerFactory;

public class MapImageDownloader {

	private static final short BUFFER_SIZE = 8192;

	private static final Logger logger = LoggerFactory.getLogger(MapImageDownloader.class);

	private InputStream connectionInputStream;

	public MapImage download(final Earthquake earthquake, final BooleanSupplier canceled) throws IOException {
		return download(earthquake, null, canceled);
	}

	public MapImage download(final Earthquake earthquake, final MapImage cached, final BooleanSupplier canceled) throws IOException {
		final Headers headers = new Headers();
		headers.set("Accept", "image/jpeg,image/*;q=0.9,*/*;q=0.8");
		headers.set("Accept-Encoding", "gzip");
		if (cached != null && cached.getEtag() != null && !cached.getEtag().trim().isEmpty()) {
			headers.set("If-None-Match", cached.getEtag());
		}
		if (canceled.getAsBoolean()) {
			logger.fine("Download canceled.");
			return null;
		}
		final HttpURLConnection connection = ConnectionFactory.makeGetRequest(earthquake.getEnclosureUrl(), headers);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
			return cached; // Not modified.
		}
		else {
			return parseResponseContent(connection, cached, canceled);
		}
	}

	private MapImage parseResponseContent(final URLConnection connection, final MapImage cached, final BooleanSupplier canceled) throws IOException {
		final String responseContentEncoding = connection.getContentEncoding();
		final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
		try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw; final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			this.connectionInputStream = raw;
			IOUtils.copy(in, out, BUFFER_SIZE);
			final MapImage downloaded = new MapImage(out.toByteArray(), connection.getHeaderField("Etag"));
			if (downloaded.equals(cached)) {
				logger.fine("downloaded.equals(cached)");
				return cached;
			}
			else {
				return downloaded;
			}
		}
		catch (final IOException e) {
			if (canceled.getAsBoolean()) {
				logger.log(Level.FINE, "Download canceled:", e);
				return null;
			}
			else {
				throw e;
			}
		}
	}

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
