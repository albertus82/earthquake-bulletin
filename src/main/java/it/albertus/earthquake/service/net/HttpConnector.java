package it.albertus.earthquake.service.net;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.net.URLConnection;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.util.Configuration;
import it.albertus.util.Version;

public class HttpConnector {

	public static class Defaults {
		public static final int CONNECTION_TIMEOUT_IN_MILLIS = 20000;
		public static final int READ_TIMEOUT_IN_MILLIS = 20000;
		public static final boolean PROXY_ENABLED = false;
		public static final Type PROXY_TYPE = Type.HTTP;
		public static final String PROXY_ADDRESS = "10.0.0.1";
		public static final int PROXY_PORT = 8080;
		public static final boolean PROXY_AUTH_REQUIRED = false;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final String USER_AGENT = String.format("Mozilla/5.0 (%s; %s; %s) EarthquakeBulletin/%s (KHTML, like Gecko)", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), Version.getInstance().getNumber());

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private HttpConnector() {
		throw new IllegalAccessError();
	}

	public static HttpURLConnection getConnection(final URL url) throws IOException {
		final URLConnection connection;
		if (configuration.getBoolean("proxy.enabled", Defaults.PROXY_ENABLED)) {
			if (configuration.getBoolean("proxy.auth.required", Defaults.PROXY_AUTH_REQUIRED)) {
				Authenticator.setDefault(new Authenticator() {
					@Override
					public PasswordAuthentication getPasswordAuthentication() {
						return (new PasswordAuthentication(configuration.getString("proxy.user"), configuration.getCharArray("proxy.password")));
					}
				});
			}
			final Proxy proxy = new Proxy(Proxy.Type.valueOf(configuration.getString("proxy.type", Defaults.PROXY_TYPE.name())), new InetSocketAddress(configuration.getString("proxy.address", Defaults.PROXY_ADDRESS), configuration.getInt("proxy.port", Defaults.PROXY_PORT)));
			connection = url.openConnection(proxy);
		}
		else {
			connection = url.openConnection(/* DIRECT */);
		}
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
