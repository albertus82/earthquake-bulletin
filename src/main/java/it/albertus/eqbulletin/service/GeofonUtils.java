package it.albertus.eqbulletin.service;

import java.net.URI;
import java.net.URISyntaxException;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeofonUtils {

	public static final String OLD_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de/old";
	public static final String NEW_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de";

	public static final String DEFAULT_GEOFON_BASE_URL = OLD_GEOFON_BASE_URL;

	public static final String MOMENT_TENSOR_FILENAME = "mt.txt";
	private static final String BEACH_BALL_FILENAME = "bb.png";

	public static URI toURI(final String spec) {
		try {
			return new URI(spec);
		}
		catch (final URISyntaxException e) {
			log.warn("Invalid URL: \"" + spec + "\":", e);
			return null;
		}
	}

	public static URI getEventMapUri(final String guid, final int year) {
		return toURI(getEventBaseUrl(guid, year) + guid + ".jpg");
	}

	public static URI getEventMomentTensorUri(final String guid, final int year) {
		return toURI(getEventBaseUrl(guid, year) + MOMENT_TENSOR_FILENAME);
	}

	public static URI getBeachBallUri(final String guid, final int year) {
		return toURI(getEventBaseUrl(guid, year) + BEACH_BALL_FILENAME);
	}

	public static String getBulletinBaseUrl() {
		return getBaseUrl() + "/eqinfo/list.php";
	}

	private static String getEventBaseUrl(final String guid, final int year) {
		return getBaseUrl() + "/data/alerts/" + year + "/" + guid + "/";
	}

	private static String getBaseUrl() {
		return EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.GEOFON_BASE_URL, DEFAULT_GEOFON_BASE_URL);
	}

}
