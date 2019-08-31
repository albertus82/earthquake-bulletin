package it.albertus.eqbulletin;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.config.LoggingConfig;
import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.util.InitializationException;
import it.albertus.util.logging.LoggingSupport;

public class EarthquakeBulletin {

	public static final String ARTIFACT_ID = "earthquake-bulletin";

	private static InitializationException initializationException;

	static {
		if (LoggingSupport.getFormat() == null) {
			LoggingSupport.setFormat(LoggingConfig.LOG_FORMAT);
		}
		try {
			EarthquakeBulletinConfig.getInstance();
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
