package it.albertus.earthquake.service.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.util.Configuration;
import it.albertus.util.Version;

public class HttpConnector {

	private static final String USER_AGENT = String.format("Mozilla/5.0 (%s; %s; %s) EarthquakeBulletin/%s (KHTML, like Gecko)", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), Version.getInstance().getNumber());

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
		urlConnection.addRequestProperty("User-Agent", USER_AGENT);
		return urlConnection;
	}

	public static HttpURLConnection openConnection(final String url) throws IOException {
		return openConnection(new URL(url));
	}

}
