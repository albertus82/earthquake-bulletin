package it.albertus.eqbulletin.config.logging;

import java.io.File;

import org.eclipse.jface.util.Util;

import ch.qos.logback.classic.Level;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.util.BuildInfo;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoggingConfigAccessor implements LoggingConfig {

	@NonNull
	private final IPreferencesConfiguration configuration;

	@Override
	public Level getConsoleLevel() {
		return Level.toLevel(configuration.getString(Preference.LOGGING_CONSOLE_LEVEL), Defaults.LOGGING_LEVEL);
	}

	@Override
	public Level getFileLevel() {
		return Level.toLevel(configuration.getString(Preference.LOGGING_FILES_LEVEL), Defaults.LOGGING_LEVEL);
	}

	@Override
	public byte getFileMaxIndex() {
		return configuration.getByte(Preference.LOGGING_FILES_COUNT, Defaults.LOGGING_FILES_MAX_INDEX);
	}

	@Override
	public int getFileMaxSize() {
		return 1024 * configuration.getShort(Preference.LOGGING_FILES_LIMIT, Defaults.LOGGING_FILES_MAX_SIZE_KB);
	}

	@Override
	public String getFileNamePattern() {
		return configuration.getString(Preference.LOGGING_FILES_PATH, Defaults.LOGGING_FILES_PATH) + File.separator + (Util.isLinux() ? BuildInfo.getProperty("project.artifactId") : BuildInfo.getProperty("project.name").replace(" ", "")) + ".%i.log";
	}

	@Override
	public String getLayoutPattern() {
		return Defaults.LOGGING_LAYOUT_PATTERN;
	}

	@Override
	public Level getRootLevel() {
		return Level.toLevel(Math.min(getConsoleLevel().toInt(), getFileLevel().toInt()));
	}

	@Override
	public boolean isFileAppenderEnabled() {
		return configuration.getBoolean(Preference.LOGGING_FILES_ENABLED, Defaults.LOGGING_FILES_ENABLED);
	}

	@Override
	public boolean isFileCompressionEnabled() {
		return configuration.getBoolean(Preference.LOGGING_FILES_COMPRESSION_ENABLED, Defaults.LOGGING_FILES_COMPRESSION_ENABLED);
	}

}
