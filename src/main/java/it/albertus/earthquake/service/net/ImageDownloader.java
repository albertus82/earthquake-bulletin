package it.albertus.earthquake.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.model.MapImage;
import it.albertus.util.Configuration;
import it.albertus.util.IOUtils;

public class ImageDownloader {

	private static final int BUFFER_SIZE = 8192;

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private ImageDownloader() {
		throw new IllegalAccessError();
	}

	public static MapImage downloadImage(final URL url, final String etag) throws IOException {
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = HttpConnector.openConnection(url);
			urlConnection.setRequestProperty("Accept", "image/*,*/*;0.9");
			if (etag != null && !etag.isEmpty()) {
				urlConnection.setReadTimeout(Math.min(3000, configuration.getInt("http.read.timeout.ms", HttpConnector.Defaults.READ_TIMEOUT_IN_MILLIS)));
				urlConnection.setRequestProperty("If-None-Match", etag);
			}
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
				return null;
			}
			else {
				try (final InputStream is = urlConnection.getInputStream(); final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					IOUtils.copy(is, buffer, BUFFER_SIZE);
					return new MapImage(buffer.toByteArray(), urlConnection.getHeaderField("etag"));
				}
			}
		}
		finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}

}
