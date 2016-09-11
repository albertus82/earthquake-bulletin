package it.albertus.geofon.client;

import it.albertus.util.Configuration;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpConnector {

	private static final Configuration configuration = GeofonClient.configuration;

	public interface Defaults {
		int CONNECTION_TIMEOUT_IN_MILLIS = 5000;
		int READ_TIMEOUT_IN_MILLIS = 5000;
	}

	public static HttpURLConnection openConnection(final URL url) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(configuration.getInt("http.connection.timeout.ms", Defaults.CONNECTION_TIMEOUT_IN_MILLIS));
		urlConnection.setReadTimeout(configuration.getInt("http.read.timeout.ms", Defaults.READ_TIMEOUT_IN_MILLIS));
		return urlConnection;
	}

	public static HttpURLConnection openConnection(final String url) throws MalformedURLException, IOException {
		return openConnection(new URL(url));
	}

}
