package it.albertus.eqbulletin.service.net;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.util.BuildInfo;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionFactory {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final int CONNECTION_TIMEOUT_IN_MILLIS = 20000;
		public static final int READ_TIMEOUT_IN_MILLIS = 20000;
		public static final boolean PROXY_ENABLED = false;
		public static final boolean PROXY_MANUAL = false;
		public static final Type PROXY_TYPE = Type.HTTP;
		public static final String PROXY_ADDRESS = "10.0.0.1";
		public static final int PROXY_PORT = 8080;
		public static final boolean PROXY_AUTH_REQUIRED = false;
	}

	private static final String USER_AGENT = String.format("Mozilla/5.0 (%s; %s; %s) EarthquakeBulletin/%s (KHTML, like Gecko)", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), BuildInfo.getProperty("project.version"));

	private static final Collection<Integer> httpRedirectionResponseCodes = Arrays.asList(HttpURLConnection.HTTP_MOVED_PERM, HttpURLConnection.HTTP_MOVED_TEMP);

	private static final byte REDIRECTION_LIMIT = 20;

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	public static HttpURLConnection makeGetRequest(@NonNull URL url, final Headers headers) throws IOException {
		HttpURLConnection urlConnection = prepareConnection(url, headers);
		byte redirectionCounter = 0;
		while (httpRedirectionResponseCodes.contains(urlConnection.getResponseCode())) { // Connection starts here
			final String location = urlConnection.getHeaderField("Location");
			if (location == null || location.isEmpty()) {
				throw new IllegalStateException("URL \"" + url + "\": server responded with HTTP " + urlConnection.getResponseCode() + " omitting the \"Location\" header.");
			}
			if (redirectionCounter++ >= REDIRECTION_LIMIT) {
				throw new IllegalStateException("The page \"" + url + "\" isn't redirecting properly.");
			}
			final String spec;
			if (location.startsWith("/")) { // Relative
				final String baseUrl = url.getProtocol() + "://" + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort() : "");
				spec = baseUrl + location;
			}
			else { // Absolute
				spec = location;
			}
			log.debug("Redirecting from \"{}\" to \"{}\"", url, spec);
			url = new URL(spec);
			urlConnection = prepareConnection(url, headers);
		}
		return urlConnection;
	}

	private static HttpURLConnection prepareConnection(@NonNull final URL url, final Headers headers) throws IOException {
		final HttpURLConnection urlConnection = createConnection(url);
		if (headers != null) {
			for (final Entry<String, List<String>> header : headers.entrySet()) {
				for (final String value : header.getValue()) {
					urlConnection.addRequestProperty(header.getKey(), value);
				}
			}
		}
		return urlConnection;
	}

	private static HttpURLConnection createConnection(@NonNull final URL url) throws IOException {
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
							return new PasswordAuthentication(configuration.getString(Preference.PROXY_USERNAME), configuration.getCharArray(Preference.PROXY_PASSWORD));
						}
					});
				}
				else {
					Authenticator.setDefault(null);
				}
				proxy = new Proxy(Proxy.Type.valueOf(configuration.getString(Preference.PROXY_TYPE, Defaults.PROXY_TYPE.name())), new InetSocketAddress(configuration.getString(Preference.PROXY_ADDRESS, Defaults.PROXY_ADDRESS), configuration.getInt(Preference.PROXY_PORT, Defaults.PROXY_PORT)));
			}
			log.debug("Using proxy: {}", proxy);
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

}
