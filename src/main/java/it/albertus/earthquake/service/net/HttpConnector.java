package it.albertus.earthquake.service.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.util.Configuration;
import it.albertus.util.Version;

public class HttpConnector {

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private static final String VERSION_NUMBER = Version.getInstance().getNumber();

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
		final StringBuilder userAgent = new StringBuilder("Mozilla/5.0");
		userAgent.append(" (").append(System.getProperty("os.name")).append("; ").append(System.getProperty("os.arch")).append("; ").append(System.getProperty("os.version")).append(") ");
		userAgent.append("EarthquakeBulletin/").append(VERSION_NUMBER).append(" (KHTML, like Gecko)");
		urlConnection.addRequestProperty("User-Agent", userAgent.toString());
		return urlConnection;
	}

	public static HttpURLConnection openConnection(final String url) throws IOException {
		return openConnection(new URL(url));
	}

}
