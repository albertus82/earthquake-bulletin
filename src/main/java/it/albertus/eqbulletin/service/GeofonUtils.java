package it.albertus.eqbulletin.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.util.logging.LoggerFactory;

public class GeofonUtils {

	public static final String MOMENT_TENSOR_FILENAME = "mt.txt";

	public static final String DEFAULT_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de";

	private static final String MSG_KEY_ERR_URL_MALFORMED = "err.url.malformed";

	private static final Logger logger = LoggerFactory.getLogger(GeofonUtils.class);

	public static URI toURI(final String spec) {
		try {
			return new URI(spec);
		}
		catch (final URISyntaxException e) {
			logger.log(Level.WARNING, Messages.get(MSG_KEY_ERR_URL_MALFORMED, spec), e);
			return null;
		}
	}

	public static URI getEventMapUri(final String guid, final int year) {
		return toURI(getEventBaseUrl(guid, year) + guid + ".jpg");
	}

	public static URI getEventMomentTensorUri(final String guid, final int year) {
		return toURI(getEventBaseUrl(guid, year) + MOMENT_TENSOR_FILENAME);
	}

	public static URI getEventMomentTensorImageUri(final String guid, final int year) {
		return toURI(getEventBaseUrl(guid, year) + "bb.png");
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

	private GeofonUtils() {
		throw new IllegalAccessError("Utility class");
	}

}
