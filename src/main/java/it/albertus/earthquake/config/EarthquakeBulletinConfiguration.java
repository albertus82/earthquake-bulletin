package it.albertus.earthquake.config;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.util.Configuration;
import it.albertus.util.logging.CustomFormatter;
import it.albertus.util.logging.FileHandlerBuilder;
import it.albertus.util.logging.LoggerFactory;
import it.albertus.util.logging.LoggingSupport;

public class EarthquakeBulletinConfiguration extends Configuration {

	private static final Logger logger = LoggerFactory.getLogger(EarthquakeBulletinConfiguration.class);

	public static class Defaults {
		public static final boolean LOGGING_FILES_ENABLED = true;
		public static final Level LOGGING_LEVEL = Level.INFO;
		public static final String LOGGING_FILES_PATH = Configuration.getOsSpecificLocalAppDataDir() + File.separator + Messages.get("msg.application.name");
		public static final int LOGGING_FILES_LIMIT = 1024;
		public static final int LOGGING_FILES_COUNT = 5;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	public static final String CFG_FILE_NAME = "earthquake-bulletin.cfg";
	public static final String LOG_FILE_NAME = "earthquake-bulletin.%g.log";

	private FileHandlerBuilder fileHandlerBuilder; // do NOT set any value here!
	private FileHandler fileHandler; // do NOT set any value here!

	public EarthquakeBulletinConfiguration() throws IOException {
		super(Messages.get("msg.application.name") + File.separator + CFG_FILE_NAME, true);
	}

	@Override
	protected void load() throws IOException {
		super.load();

		// Language
		final String language = getString("language", Messages.Defaults.LANGUAGE);
		Messages.setLanguage(language);
		JFaceMessages.setLanguage(language);

		// Logging
		if (this != null) {
			try {
				LoggingSupport.setLevel(LoggingSupport.getRootLogger().getName(), Level.parse(this.getString("logging.level", Defaults.LOGGING_LEVEL.getName())));
			}
			catch (final IllegalArgumentException iae) {
				logger.log(Level.WARNING, iae.toString(), iae);
			}

			if (this.getBoolean("logging.files.enabled", Defaults.LOGGING_FILES_ENABLED)) {
				final String loggingPath = this.getString("logging.files.path", Defaults.LOGGING_FILES_PATH);
				if (loggingPath != null && !loggingPath.isEmpty()) {
					final FileHandlerBuilder builder = new FileHandlerBuilder().pattern(loggingPath + File.separator + LOG_FILE_NAME).limit(this.getInt("logging.files.limit", Defaults.LOGGING_FILES_LIMIT) * 1024).count(this.getInt("logging.files.count", Defaults.LOGGING_FILES_COUNT)).append(true).formatter(new CustomFormatter(EarthquakeBulletin.LOG_FORMAT));
					if (fileHandlerBuilder == null || !builder.equals(fileHandlerBuilder)) {
						if (fileHandler != null) {
							LoggingSupport.getRootLogger().removeHandler(fileHandler);
							fileHandler.close();
							fileHandler = null;
						}
						try {
							new File(loggingPath).mkdirs();
							fileHandlerBuilder = builder;
							fileHandler = builder.build();
							LoggingSupport.getRootLogger().addHandler(fileHandler);
						}
						catch (final IOException ioe) {
							logger.log(Level.SEVERE, ioe.toString(), ioe);
						}
					}
				}
			}
			else {
				if (fileHandler != null) {
					LoggingSupport.getRootLogger().removeHandler(fileHandler);
					fileHandler.close();
					fileHandler = null;
					fileHandlerBuilder = null;
				}
			}
		}
	}

}
