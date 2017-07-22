package it.albertus.earthquake.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.albertus.util.IOUtils;

public class ImageDownloader {

	private static final int BUFFER_SIZE = 8192;

	private ImageDownloader() {
		throw new IllegalAccessError();
	}

	public static byte[] downloadImage(final URL url) throws IOException {
		InputStream is = null;
		HttpURLConnection urlConnection = null;
		try (final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
			urlConnection = HttpConnector.openConnection(url);
			urlConnection.addRequestProperty("Accept", "image/*");
			is = urlConnection.getInputStream();
			IOUtils.copy(is, buffer, BUFFER_SIZE);
			return buffer.toByteArray();
		}
		finally {
			IOUtils.closeQuietly(is);
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
	}

	public static byte[] downloadImage(final String url) throws MalformedURLException, IOException {
		return downloadImage(new URL(url));
	}

}
