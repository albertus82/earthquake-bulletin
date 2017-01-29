package it.albertus.earthquake;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;

public class EarthquakeBulletin {

	private static final Logger logger = LoggerFactory.getLogger(EarthquakeBulletin.class);

	public static class InitializationException extends Exception {
		private static final long serialVersionUID = 6499234883656892068L;

		private InitializationException(final String message, final Throwable cause) {
			super(message, cause);
		}
	}

	public static class Defaults {
		public static final String LANGUAGE = Locale.getDefault().getLanguage();

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	public static final String BASE_URL = "http://geofon.gfz-potsdam.de";
	public static final String CFG_KEY_LANGUAGE = "language";
	public static final String CFG_FILE_NAME = "earthquake-bulletin.cfg";

	private static Configuration configuration = null;

	private static InitializationException initializationException = null;

	private EarthquakeBulletin() {
		throw new IllegalAccessError();
	}

	static {
		final String parent = Messages.get("msg.application.name");
		final File config = new File((parent != null ? parent : "") + File.separator + CFG_FILE_NAME);
		try {
			configuration = new Configuration(config.getPath(), true) {
				@Override
				protected void load() throws IOException {
					super.load();
					final String language = getString(CFG_KEY_LANGUAGE, Defaults.LANGUAGE);
					Messages.setLanguage(language);
					JFaceMessages.setLanguage(language);
				}
			};
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
