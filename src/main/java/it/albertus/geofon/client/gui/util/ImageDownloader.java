package it.albertus.geofon.client.gui.util;

import it.albertus.geofon.client.HttpConnector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageDownloader {

	public static byte[] downloadImage(final URL url) throws IOException {
		InputStream is = null;
		ByteArrayOutputStream buffer = null;
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = HttpConnector.openConnection(url);
			urlConnection.addRequestProperty("Accept", "image/*");
			is = urlConnection.getInputStream();
			buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[8192];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
		}
		finally {
			try {
				buffer.close();
			}
			catch (final Exception e) {/* Ignore */}
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
			try {
				urlConnection.disconnect();
			}
			catch (final Exception e) {/* Ignore */}
		}
		return buffer.toByteArray();
	}

	public static byte[] downloadImage(final String url) throws MalformedURLException, IOException {
		return downloadImage(new URL(url));
	}

}
