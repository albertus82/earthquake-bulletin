package it.albertus.eqbulletin.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.preference.PreferencesConfiguration;
import it.albertus.util.logging.LoggerFactory;

public class GeofonUtils {

	public static final String MOMENT_TENSOR_FILENAME = "mt.txt";

	private static final String DEFAULT_BASE_URL = "https://geofon.gfz-potsdam.de";

	private static final String MSG_KEY_ERR_URL_MALFORMED = "err.url.malformed";

	private static final Logger logger = LoggerFactory.getLogger(GeofonUtils.class);

	private static final PreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	public static URL getEventPageUrl(final String guid) {
		final String spec = getBaseUrl() + "/eqinfo/event.php?id=" + guid;
		try {
			return new URL(spec);
		}
		catch (final MalformedURLException e) {
			logger.log(Level.WARNING, Messages.get(MSG_KEY_ERR_URL_MALFORMED, spec), e);
			return null;
		}
	}

	public static URL getEventMapUrl(final String guid, final int year) {
		final String spec = getEventBaseUrl(guid, year) + guid + ".jpg";
		try {
			return new URL(spec);
		}
		catch (final MalformedURLException e) {
			logger.log(Level.WARNING, Messages.get(MSG_KEY_ERR_URL_MALFORMED, spec), e);
			return null;
		}
	}

	public static URL getEventMomentTensorUrl(final String guid, final int year) {
		final String spec = getEventBaseUrl(guid, year) + MOMENT_TENSOR_FILENAME;
		try {
			return new URL(spec);
		}
		catch (final MalformedURLException e) {
			logger.log(Level.WARNING, Messages.get(MSG_KEY_ERR_URL_MALFORMED, spec), e);
			return null;
		}
	}

	public static String getBaseUrl() {
		return configuration.getString("base.url", GeofonUtils.DEFAULT_BASE_URL);
	}

	private static String getEventBaseUrl(final String guid, final int year) {
		return getBaseUrl() + "/data/alerts/" + year + "/" + guid + "/";
	}

	private GeofonUtils() {
		throw new IllegalAccessError("Utility class");
	}

}
