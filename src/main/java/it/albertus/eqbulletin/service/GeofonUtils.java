package it.albertus.eqbulletin.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.resources.Messages;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeofonUtils {

	public static final String DEFAULT_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de";

	private static final String MOMENT_TENSOR_FILENAME = "mt.txt";
	private static final String BEACH_BALL_FILENAME = "bb.png";

	private static final String MSG_KEY_ERR_URL_MALFORMED = "err.url.malformed";

	public static URI toURI(final String spec) {
		try {
			return new URI(spec);
		}
		catch (final URISyntaxException e) {
			log.log(Level.WARNING, Messages.get(MSG_KEY_ERR_URL_MALFORMED, spec), e);
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
		return EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.GEOFON_BASE_URL, GeofonUtils.DEFAULT_GEOFON_BASE_URL);
	}

}
