package com.github.albertus82.eqbulletin.config;

import java.time.DateTimeException;
import java.time.ZoneId;

import com.github.albertus82.eqbulletin.gui.preference.Preference;

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
			final String fallback = DEFAULT_ZONE_ID;
			log.warn("Cannot determine configured time-zone ID, falling back to " + fallback + ':', e);
			return ZoneId.of(fallback);
		}
	}

}
