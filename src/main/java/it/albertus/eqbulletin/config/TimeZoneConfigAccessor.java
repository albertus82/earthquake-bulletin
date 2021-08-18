package it.albertus.eqbulletin.config;

import java.time.DateTimeException;
import java.time.ZoneId;

import it.albertus.eqbulletin.gui.preference.Preference;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeZoneConfigAccessor {

	public static final String DEFAULT_ZONE_ID = "UTC";

	public static ZoneId getZoneId() {
		try {
			return ZoneId.of(EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.TIMEZONE, DEFAULT_ZONE_ID));
		}
		catch (final DateTimeException e) {
			log.warn(e.toString(), e);
			return ZoneId.of(DEFAULT_ZONE_ID);
		}
	}

}
