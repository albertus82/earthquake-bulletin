package it.albertus.geofon.client.gui.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

public class ImageDownloader {

	public static Image downloadImage(final URL url) throws IOException {
		InputStream is = null;
		Image image = null;
		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(5000);
			urlConnection.setReadTimeout(5000);
			urlConnection.addRequestProperty("Accept", "image/*");
			is = urlConnection.getInputStream();
			final ImageData[] images = new ImageLoader().load(is);
			urlConnection.disconnect();

			if (images.length > 0) {
				image = new Image(Display.getCurrent(), images[0]);
			}
		}
		finally {
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
			try {
				urlConnection.disconnect();
			}
			catch (final Exception e) {/* Ignore */}
		}
		return image;
	}

	public static Image downloadImage(final String url) throws MalformedURLException, IOException {
		return downloadImage(new URL(url));
	}

}
