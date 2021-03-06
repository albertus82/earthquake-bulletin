package it.albertus.eqbulletin.config;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.logging.Level;

import it.albertus.eqbulletin.gui.preference.Preference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeZoneConfig {

	public static final String DEFAULT_ZONE_ID = "UTC";

	public static ZoneId getZoneId() {
		try {
			return ZoneId.of(EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.TIMEZONE, DEFAULT_ZONE_ID));
		}
		catch (final DateTimeException e) {
			log.log(Level.WARNING, e.toString(), e);
			return ZoneId.of(DEFAULT_ZONE_ID);
		}
	}

}
