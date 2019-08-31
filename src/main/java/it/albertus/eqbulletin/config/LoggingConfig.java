package it.albertus.eqbulletin.config;

import static it.albertus.eqbulletin.EarthquakeBulletin.ARTIFACT_ID;

import java.io.File;
import java.util.logging.Level;

import org.eclipse.jface.util.Util;

import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.logging.LoggingDefaultConfig;

public class LoggingConfig extends LoggingDefaultConfig {

	public static final String LOG_FORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS.%tL %4$s %3$s - %5$s%6$s%n";

	public static final String LOG_FILE_NAME_PATTERN = Util.isLinux() ? ARTIFACT_ID + ".%g.log" : "EarthquakeBulletin.%g.log";
	public static final String DEFAULT_LOGGING_FILES_PATH = EarthquakeBulletinConfig.APPDATA_DIRECTORY + File.separator + "log";
	public static final Level DEFAULT_LOGGING_LEVEL = Level.WARNING;

	private final IPreferencesConfiguration configuration;

	LoggingConfig(final IPreferencesConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public boolean isFileHandlerEnabled() {
		return configuration.getBoolean(Preference.LOGGING_FILES_ENABLED, super.isFileHandlerEnabled());
	}

	@Override
	public String getLoggingLevel() {
		return configuration.getString(Preference.LOGGING_LEVEL, DEFAULT_LOGGING_LEVEL.getName());
	}

	@Override
	public String getFileHandlerPattern() {
		return configuration.getString(Preference.LOGGING_FILES_PATH, DEFAULT_LOGGING_FILES_PATH) + File.separator + LOG_FILE_NAME_PATTERN;
	}

	@Override
	public int getFileHandlerLimit() {
		final Integer limit = configuration.getInt(Preference.LOGGING_FILES_LIMIT);
		if (limit != null) {
			return limit * 1024;
		}
		else {
			return super.getFileHandlerLimit();
		}
	}

	@Override
	public int getFileHandlerCount() {
		return configuration.getInt(Preference.LOGGING_FILES_COUNT, super.getFileHandlerCount());
	}

	@Override
	public String getFileHandlerFormat() {
		return LOG_FORMAT;
	}

}
