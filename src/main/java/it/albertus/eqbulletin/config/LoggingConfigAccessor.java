package it.albertus.eqbulletin.config;

import static it.albertus.eqbulletin.config.EarthquakeBulletinConfig.APPDATA_DIRECTORY;
import static org.slf4j.event.Level.WARN;

import java.io.File;

import org.eclipse.jface.util.Util;
import org.slf4j.event.Level;

import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.util.BuildInfo;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.logging.LoggingDefaultConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LoggingConfigAccessor extends LoggingDefaultConfig {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final String LOGGING_FILES_PATH = APPDATA_DIRECTORY + File.separator + "log";
		public static final Level LOGGING_LEVEL = WARN;
	}

	@NonNull
	private final IPreferencesConfiguration configuration;

	@Override
	public boolean isFileHandlerEnabled() {
		return configuration.getBoolean(Preference.LOGGING_FILES_ENABLED, super.isFileHandlerEnabled());
	}

	@Override
	public String getLoggingLevel() {
		return configuration.getString(Preference.LOGGING_LEVEL, Defaults.LOGGING_LEVEL.toString());
	}

	@Override
	public String getFileHandlerPattern() {
		return configuration.getString(Preference.LOGGING_FILES_PATH, Defaults.LOGGING_FILES_PATH) + File.separator + (Util.isLinux() ? BuildInfo.getProperty("project.artifactId") : BuildInfo.getProperty("project.name").replace(" ", "")) + ".%i.log";
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
		return "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n";
	}

}
