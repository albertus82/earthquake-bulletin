package it.albertus.earthquake.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.util.Configuration;

public class HttpConnector {

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	public static class Defaults {
		public static final int CONNECTION_TIMEOUT_IN_MILLIS = 20000;
		public static final int READ_TIMEOUT_IN_MILLIS = 20000;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private HttpConnector() {
		throw new IllegalAccessError();
	}

	public static HttpURLConnection openConnection(final URL url) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(configuration.getInt("http.connection.timeout.ms", Defaults.CONNECTION_TIMEOUT_IN_MILLIS));
		urlConnection.setReadTimeout(configuration.getInt("http.read.timeout.ms", Defaults.READ_TIMEOUT_IN_MILLIS));
		urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0");
		return urlConnection;
	}

	public static HttpURLConnection openConnection(final String url) throws MalformedURLException, IOException {
		return openConnection(new URL(url));
	}

}
