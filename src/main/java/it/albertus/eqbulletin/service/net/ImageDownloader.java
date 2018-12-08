package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.IOUtils;
import it.albertus.util.logging.LoggerFactory;

public class ImageDownloader {

	private static final short BUFFER_SIZE = 8192;

	private static final Logger logger = LoggerFactory.getLogger(ImageDownloader.class);

	private static final Collection<Integer> httpRedirectionResponseCodes = Arrays.asList(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP);

	private static final byte REDIRECTION_LIMIT = 20;

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private ImageDownloader() {
		throw new IllegalAccessError();
	}

	public static MapImage downloadImage(final URL url, final String etag) throws IOException {
		final HttpURLConnection urlConnection = connect(url, etag);
		try {
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
				return null;
			}
			else {
				final String responseContentEncoding = urlConnection.getContentEncoding();
				final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
				try (final InputStream internalInputStream = urlConnection.getInputStream(); final InputStream inputStream = gzip ? new GZIPInputStream(internalInputStream) : internalInputStream; final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					IOUtils.copy(inputStream, buffer, BUFFER_SIZE);
					return new MapImage(buffer.toByteArray(), urlConnection.getHeaderField("etag"));
				}
			}
		}
		finally {
			urlConnection.disconnect();
		}
	}

	private static HttpURLConnection connect(URL url, final String etag) throws IOException {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = prepareConnection(url, etag);
			byte redirectionCounter = 0;
			while (httpRedirectionResponseCodes.contains(urlConnection.getResponseCode())) { // Connection starts here
				final String location = urlConnection.getHeaderField("Location");
				urlConnection.disconnect();
				if (location == null || location.isEmpty()) {
					throw new IllegalStateException("URL \"" + url + "\": server responded with HTTP " + urlConnection.getResponseCode() + " omitting the \"Location\" header.");
				}
				if (redirectionCounter++ >= REDIRECTION_LIMIT) {
					throw new IllegalStateException("The page \"" + url + "\" isn't redirecting properly.");
				}
				final String spec;
				if (location.startsWith("/")) { // Relative
					final String baseUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort() : "");
					spec = baseUrl + location;
				}
				else { // Absolute
					spec = location;
				}
				logger.log(Level.FINE, "Redirecting from \"{0}\" to \"{1}\"", new Serializable[] { url, spec });
				url = new URL(spec);
				urlConnection = prepareConnection(url, etag);
			}
			return urlConnection;
		}
		catch (final Exception e) {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			throw e;
		}
	}

	private static HttpURLConnection prepareConnection(final URL url, final String etag) throws IOException {
		final HttpURLConnection urlConnection = ConnectionFactory.createHttpConnection(url);
		urlConnection.setRequestProperty("Accept", "image/*,*/*;0.9");
		urlConnection.setRequestProperty("Accept-Encoding", "gzip");
		if (etag != null && !etag.isEmpty()) {
			urlConnection.setReadTimeout(Math.min(3000, configuration.getInt(Preference.HTTP_READ_TIMEOUT_MS, ConnectionFactory.Defaults.READ_TIMEOUT_IN_MILLIS)));
			urlConnection.setRequestProperty("If-None-Match", etag);
		}
		return urlConnection;
	}

}
