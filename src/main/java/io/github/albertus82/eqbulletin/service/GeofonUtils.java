package io.github.albertus82.eqbulletin.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.albertus82.eqbulletin.model.Format;
import io.github.albertus82.eqbulletin.service.decode.html.HtmlBulletinVersion;
import io.github.albertus82.eqbulletin.service.net.ConnectionUtils;
import io.github.albertus82.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeofonUtils {

	public static final String DEFAULT_GEOFON_BASE_URL = "https://geofon.gfz.de";

	public static final String MOMENT_TENSOR_FILENAME = "mt.txt";
	private static final String BEACH_BALL_FILENAME = "bb.png";

	private static final String EQINFO_LIST_PHP = "/eqinfo/list.php";

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	public static URI getEventLinkUri(@NonNull final String guid) throws MalformedURLException, URISyntaxException {
		return fixOldGeofonBaseUrl(ConnectionUtils.toURI(getBaseUrl() + "/eqinfo/event.php?id=" + guid));
	}

	public static URI getEventMapUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return fixOldGeofonBaseUrl(ConnectionUtils.toURI(getEventBaseUrl(guid, year) + guid + ".jpg"));
	}

	public static URI getEventMomentTensorUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return fixOldGeofonBaseUrl(ConnectionUtils.toURI(getEventBaseUrl(guid, year) + MOMENT_TENSOR_FILENAME));
	}

	public static URI getBeachBallUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return fixOldGeofonBaseUrl(ConnectionUtils.toURI(getEventBaseUrl(guid, year) + BEACH_BALL_FILENAME));
	}

	public static String getBulletinBaseUrl(@NonNull Format format) throws MalformedURLException {
		if (Format.QUAKEML.equals(format)) {
			return getBaseUrl() + "/fdsnws/event/1/query";
		}
		else if (Format.HTML.equals(format)) {
			String baseUrl = getBaseUrl();
			final HtmlBulletinVersion version = HtmlBulletinVersion.forValue(configuration.getString(Preference.HTML_BULLETIN_VERSION));
			switch (version) {
			case NEW:
				if (baseUrl.endsWith("/old")) {
					baseUrl = baseUrl.substring(0, baseUrl.indexOf("/old"));
				}
				return baseUrl + EQINFO_LIST_PHP;
			case OLD:
				if (!baseUrl.endsWith("/old")) {
					baseUrl += "/old";
				}
				return baseUrl + EQINFO_LIST_PHP;
			default:
				throw new UnsupportedOperationException(String.valueOf(version));
			}
		}
		else {
			return getBaseUrl() + EQINFO_LIST_PHP;
		}
	}

	private static String getEventBaseUrl(@NonNull final String guid, final int year) throws MalformedURLException {
		return getBaseUrl() + "/data/alerts/" + year + "/" + guid + "/";
	}

	private static String getBaseUrl() throws MalformedURLException {
		final String spec = EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.GEOFON_BASE_URL, DEFAULT_GEOFON_BASE_URL);
		return ConnectionUtils.sanitizeUriString(spec);
	}

	/** Fix FileNotFoundException (404) for resources when using the old base URL */
	private static URI fixOldGeofonBaseUrl(URI uri) {
		final String uriStr = uri.toString();
		if (uriStr.contains("/old")) {
			uri = URI.create(uriStr.replace("/old", ""));
		}
		return uri;
	}

}
