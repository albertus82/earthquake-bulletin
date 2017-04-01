package it.albertus.earthquake;

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

	public static class Defaults {
		public static final String TIME_ZONE_ID = "UTC";

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

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

	static {
		if (LoggingSupport.getFormat() == null) {
			LoggingSupport.setFormat(LOG_FORMAT);
		}
		logger = LoggerFactory.getLogger(EarthquakeBulletin.class);
		try {
			configuration = EarthquakeBulletinConfiguration.getInstance();
		}
		catch (final RuntimeException e) {
			final String message = Messages.get("err.open.cfg", EarthquakeBulletinConfiguration.CFG_FILE_NAME);
			logger.log(Level.SEVERE, message, e);
			initializationException = new InitializationException(message, e);
		}
	}

	private EarthquakeBulletin() {
		throw new IllegalAccessError();
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
