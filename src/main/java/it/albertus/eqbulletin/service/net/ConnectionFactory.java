package it.albertus.eqbulletin.service.net;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.Version;
import it.albertus.util.logging.LoggerFactory;

public class ConnectionFactory {

	public static class Defaults {
		public static final int CONNECTION_TIMEOUT_IN_MILLIS = 20000;
		public static final int READ_TIMEOUT_IN_MILLIS = 20000;
		public static final boolean PROXY_ENABLED = false;
		public static final boolean PROXY_MANUAL = false;
		public static final Type PROXY_TYPE = Type.HTTP;
		public static final String PROXY_ADDRESS = "10.0.0.1";
		public static final int PROXY_PORT = 8080;
		public static final boolean PROXY_AUTH_REQUIRED = false;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final String USER_AGENT = String.format("Mozilla/5.0 (%s; %s; %s) EarthquakeBulletin/%s (KHTML, like Gecko)", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), Version.getInstance().getNumber());

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(ConnectionFactory.class);

	private ConnectionFactory() {
		throw new IllegalAccessError();
	}

	public static HttpURLConnection createHttpConnection(final URL url) throws IOException {
		final URLConnection connection;
		if (configuration.getBoolean(Preference.PROXY_ENABLED, Defaults.PROXY_ENABLED)) {
			Proxy proxy;
			if (!configuration.getBoolean(Preference.PROXY_MANUAL, Defaults.PROXY_MANUAL)) {
				System.setProperty("java.net.useSystemProxies", Boolean.TRUE.toString());
				try {
					proxy = ProxySelector.getDefault().select(url.toURI()).get(0);
				}
				catch (final URISyntaxException e) {
					throw new IllegalArgumentException(url.toString(), e);
				}
			}
			else {
				if (configuration.getBoolean(Preference.PROXY_AUTH_REQUIRED, Defaults.PROXY_AUTH_REQUIRED)) {
					System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
					System.setProperty("http.maxRedirects", "2"); // limit proxy authentication retries
					Authenticator.setDefault(new Authenticator() {
						@Override
						public PasswordAuthentication getPasswordAuthentication() {
							return (new PasswordAuthentication(configuration.getString(Preference.PROXY_USERNAME), configuration.getCharArray(Preference.PROXY_PASSWORD)));
						}
					});
				}
				else {
					Authenticator.setDefault(null);
				}
				proxy = new Proxy(Proxy.Type.valueOf(configuration.getString(Preference.PROXY_TYPE, Defaults.PROXY_TYPE.name())), new InetSocketAddress(configuration.getString(Preference.PROXY_ADDRESS, Defaults.PROXY_ADDRESS), configuration.getInt(Preference.PROXY_PORT, Defaults.PROXY_PORT)));
			}
			logger.log(Level.CONFIG, "Using proxy: {0}", proxy);
			connection = url.openConnection(proxy);
		}
		else {
			Authenticator.setDefault(null);
			connection = url.openConnection(/* DIRECT */);
		}
		if (connection instanceof HttpURLConnection) {
			connection.setConnectTimeout(configuration.getInt(Preference.HTTP_CONNECTION_TIMEOUT_MS, Defaults.CONNECTION_TIMEOUT_IN_MILLIS));
			connection.setReadTimeout(configuration.getInt(Preference.HTTP_READ_TIMEOUT_MS, Defaults.READ_TIMEOUT_IN_MILLIS));
			connection.setRequestProperty("User-Agent", USER_AGENT);
			return (HttpURLConnection) connection;
		}
		else {
			throw new IllegalArgumentException(String.valueOf(url));
		}
	}

	public static HttpURLConnection createHttpConnection(final String url) throws IOException {
		return createHttpConnection(new URL(url));
	}

	public static void main(final String... args) throws URISyntaxException {
		System.setProperty("java.net.useSystemProxies", "true");
		System.out.println("detecting proxies");
		for (final Proxy proxy : ProxySelector.getDefault().select(new URI("https://foo/bar"))) {
			System.out.println("proxy type: " + proxy.type());
			final InetSocketAddress addr = (InetSocketAddress) proxy.address();
			if (addr == null) {
				System.out.println("No Proxy");
			}
			else {
				System.out.println("proxy hostname: " + addr.getHostName());
				System.setProperty("http.proxyHost", addr.getHostName());
				System.out.println("proxy port: " + addr.getPort());
				System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
			}
		}
	}

}
