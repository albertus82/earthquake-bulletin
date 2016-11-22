package it.albertus.earthquake;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.util.Configuration;

import java.io.File;
import java.util.Locale;

public class EarthquakeBulletin {

	public interface Defaults {
		String LANGUAGE = Locale.getDefault().getLanguage();
	}

	public static final String BASE_URL = "http://geofon.gfz-potsdam.de";
	public static final String CFG_KEY_LANGUAGE = "language";
	public static final String CFG_FILE_NAME = "earthquake-bulletin.cfg";
	public static final Configuration configuration;

	static {
		File config = null;
		try {
			final String parent = System.getProperty("user.home") + File.separator + Messages.get("msg.application.name");
			config = new File((parent != null ? parent : "") + File.separator + CFG_FILE_NAME);
		}
		catch (final Exception e) {
			config = new File(CFG_FILE_NAME);
		}
		configuration = new Configuration(config.getPath()) {
			@Override
			protected void load() {
				super.load();
				final String language = getString(CFG_KEY_LANGUAGE, Defaults.LANGUAGE);
				Messages.setLanguage(language);
				JFaceMessages.setLanguage(language);
			};
		};
	}

	public static void main(final String[] args) {
		EarthquakeBulletinGui.run();
	}

}
