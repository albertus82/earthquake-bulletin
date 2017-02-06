package it.albertus.earthquake;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.resources.Messages;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;
import it.albertus.util.logging.LoggingSupport;

public class EarthquakeBulletin {

	private static final Logger logger = LoggerFactory.getLogger(EarthquakeBulletin.class);

	public static class InitializationException extends Exception {
		private static final long serialVersionUID = 6499234883656892068L;

		private InitializationException(final String message, final Throwable cause) {
			super(message, cause);
		}
	}

	public static final String BASE_URL = "http://geofon.gfz-potsdam.de";
	public static final String CFG_FILE_NAME = "earthquake-bulletin.cfg";
	public static final String LOG_FILE_NAME = "earthquake-bulletin.%g.log";

	private static Configuration configuration = null;
	private static InitializationException initializationException = null;

	private EarthquakeBulletin() {
		throw new IllegalAccessError();
	}

	static {
		if (LoggingSupport.getFormat() == null) {
			LoggingSupport.setFormat("%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS %4$s: %5$s%6$s%n");
		}
		final String parent = Messages.get("msg.application.name");
		final File config = new File((parent != null ? parent : "") + File.separator + CFG_FILE_NAME);
		try {
			configuration = new EarthquakeBulletinConfiguration(config.getPath(), true);
		}
		catch (final IOException ioe) {
			final String message = Messages.get("err.open.cfg", CFG_FILE_NAME);
			logger.log(Level.SEVERE, message, ioe);
			initializationException = new InitializationException(message, ioe);
		}

	}

	public static void main(final String[] args) {
		EarthquakeBulletinGui.run(initializationException);
	}

	public static Configuration getConfiguration() {
		return configuration;
	}

	public static InitializationException getInitializationException() {
		return initializationException;
	}

}
