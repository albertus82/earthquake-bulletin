package it.albertus.eqbulletin.config;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.util.logging.LoggerFactory;

public class TimeZoneConfig {

	private static final Logger logger = LoggerFactory.getLogger(TimeZoneConfig.class);

	public static final String DEFAULT_ZONE_ID = "UTC";

	public static ZoneId getZoneId() {
		try {
			return ZoneId.of(EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.TIMEZONE, DEFAULT_ZONE_ID));
		}
		catch (final DateTimeException e) {
			logger.log(Level.WARNING, e.toString(), e);
			return ZoneId.of(DEFAULT_ZONE_ID);
		}
	}

	private TimeZoneConfig() {
		throw new IllegalAccessError("Utility class");
	}

}
