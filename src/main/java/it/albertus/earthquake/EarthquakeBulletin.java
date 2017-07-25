package it.albertus.earthquake;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.util.InitializationException;
import it.albertus.util.logging.LoggingSupport;

public class EarthquakeBulletin {

	public static class Defaults {
		public static final String TIME_ZONE_ID = "UTC";

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	public static final String LOG_FORMAT = "%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS.%tL %4$s %3$s - %5$s%6$s%n";

	private static InitializationException initializationException;

	static {
		if (LoggingSupport.getFormat() == null) {
			LoggingSupport.setFormat(LOG_FORMAT);
		}
		try {
			EarthquakeBulletinConfiguration.getInstance();
		}
		catch (final InitializationException e) {
			initializationException = e;
		}
		catch (final RuntimeException e) {
			initializationException = new InitializationException(e.getMessage(), e);
		}
	}

	private EarthquakeBulletin() {
		throw new IllegalAccessError();
	}

	public static void main(final String[] args) {
		EarthquakeBulletinGui.run(initializationException);
	}

	public static InitializationException getInitializationException() {
		return initializationException;
	}

}
