package it.albertus.earthquake.service.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

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

	public static HttpURLConnection getConnection(final URL url) throws IOException {
		final URLConnection connection = url.openConnection();
		if (connection instanceof HttpURLConnection) {
			connection.setConnectTimeout(configuration.getInt("http.connection.timeout.ms", Defaults.CONNECTION_TIMEOUT_IN_MILLIS));
			connection.setReadTimeout(configuration.getInt("http.read.timeout.ms", Defaults.READ_TIMEOUT_IN_MILLIS));
			connection.setRequestProperty("User-Agent", USER_AGENT);
			return (HttpURLConnection) connection;
		}
		else {
			throw new IllegalArgumentException(String.valueOf(url));
		}
	}

	public static HttpURLConnection getConnection(final String url) throws IOException {
		return getConnection(new URL(url));
	}

}
