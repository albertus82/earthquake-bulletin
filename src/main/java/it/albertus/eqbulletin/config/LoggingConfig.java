package it.albertus.eqbulletin.config;

import static it.albertus.eqbulletin.EarthquakeBulletin.ARTIFACT_ID;
import static it.albertus.eqbulletin.config.EarthquakeBulletinConfig.APPDATA_DIRECTORY;
import static java.util.logging.Level.WARNING;

import java.io.File;
import java.util.logging.Level;

import org.eclipse.jface.util.Util;

import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.logging.LoggingDefaultConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LoggingConfig extends LoggingDefaultConfig {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final String LOGGING_FILES_PATH = APPDATA_DIRECTORY + File.separator + "log";
		public static final Level LOGGING_LEVEL = WARNING;
	}

	private final @NonNull IPreferencesConfiguration configuration;

	@Override
	public boolean isFileHandlerEnabled() {
		return configuration.getBoolean(Preference.LOGGING_FILES_ENABLED, super.isFileHandlerEnabled());
	}

	@Override
	public String getLoggingLevel() {
		return configuration.getString(Preference.LOGGING_LEVEL, Defaults.LOGGING_LEVEL.getName());
	}

	@Override
	public String getFileHandlerPattern() {
		return configuration.getString(Preference.LOGGING_FILES_PATH, Defaults.LOGGING_FILES_PATH) + File.separator + (Util.isLinux() ? ARTIFACT_ID + ".%g.log" : "EarthquakeBulletin.%g.log");
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

}
