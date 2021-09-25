package it.albertus.eqbulletin.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeofonUtils {

	public static final String OLD_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de/old";
	public static final String NEW_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de";

	public static final String DEFAULT_GEOFON_BASE_URL = OLD_GEOFON_BASE_URL;

	public static final String MOMENT_TENSOR_FILENAME = "mt.txt";
	private static final String BEACH_BALL_FILENAME = "bb.png";

	public static URI toURI(@NonNull final String spec) throws MalformedURLException, URISyntaxException {
		return new URI(sanitizeUriString(spec));
	}

	public static URI getEventMapUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return toURI(getEventBaseUrl(guid, year) + guid + ".jpg");
	}

	public static URI getEventMomentTensorUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return toURI(getEventBaseUrl(guid, year) + MOMENT_TENSOR_FILENAME);
	}

	public static URI getBeachBallUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return toURI(getEventBaseUrl(guid, year) + BEACH_BALL_FILENAME);
	}

	public static String getBulletinBaseUrl() throws MalformedURLException {
		return getBaseUrl() + "/eqinfo/list.php";
	}

	private static String getEventBaseUrl(@NonNull final String guid, final int year) throws MalformedURLException {
		return getBaseUrl() + "/data/alerts/" + year + "/" + guid + "/";
	}

	private static String getBaseUrl() throws MalformedURLException {
		final String spec = EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.GEOFON_BASE_URL, DEFAULT_GEOFON_BASE_URL);
		return sanitizeUriString(spec);
	}

	private static String sanitizeUriString(@NonNull final String spec) throws MalformedURLException {
		final String sanitized = spec.trim();
		final String lower = sanitized.toLowerCase(Locale.ROOT);
		if (!lower.startsWith("http:") && !lower.startsWith("https:")) {
			throw new MalformedURLException("Illegal or missing protocol (only http and https are allowed)");
		}
		return sanitized;
	}

}
