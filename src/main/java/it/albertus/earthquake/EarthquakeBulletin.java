package it.albertus.earthquake;

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

	private static final Logger logger;

	public static class InitializationException extends Exception {
		private static final long serialVersionUID = 6499234883656892068L;

		private InitializationException(final String message, final Throwable cause) {
			super(message, cause);
		}
	}

	public static final String BASE_URL = "http://geofon.gfz-potsdam.de";
	public static final String LOG_FORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS.%tL %4$s %3$s - %5$s%6$s%n";

	private static Configuration configuration;
	private static InitializationException initializationException;

	private EarthquakeBulletin() {
		throw new IllegalAccessError();
	}

	static {
		if (LoggingSupport.getFormat() == null) {
			LoggingSupport.setFormat(LOG_FORMAT);
		}
		logger = LoggerFactory.getLogger(EarthquakeBulletin.class);
		try {
			configuration = new EarthquakeBulletinConfiguration();
		}
		catch (final IOException ioe) {
			final String message = Messages.get("err.open.cfg", EarthquakeBulletinConfiguration.CFG_FILE_NAME);
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
