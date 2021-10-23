package com.github.albertus82.eqbulletin.config.logging;

import static ch.qos.logback.classic.Level.WARN;
import static com.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig.APPDATA_DIRECTORY;

import java.io.File;

import ch.qos.logback.classic.Level;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public interface LoggingConfig {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	class Defaults {
		public static final boolean LOGGING_FILES_COMPRESSION_ENABLED = false;
		public static final boolean LOGGING_FILES_ENABLED = true;
		public static final byte LOGGING_FILES_MAX_INDEX = 5;
		public static final short LOGGING_FILES_MAX_SIZE_KB = 1024;
		public static final String LOGGING_FILES_PATH = APPDATA_DIRECTORY + File.separator + "log";
		public static final String LOGGING_LAYOUT_PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n";
		public static final Level LOGGING_LEVEL = WARN;
	}

	Level getConsoleLevel();

	Level getFileLevel();

	byte getFileMaxIndex();

	int getFileMaxSize();

	String getFileNamePattern();

	String getLayoutPattern();

	Level getRootLevel();

	boolean isFileAppenderEnabled();

	boolean isFileCompressionEnabled();

}
