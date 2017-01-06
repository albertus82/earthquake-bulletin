package it.albertus.earthquake;

import java.io.File;
import java.util.Locale;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.util.Configuration;

public class EarthquakeBulletin {

	public static class Defaults {
		public static final String LANGUAGE = Locale.getDefault().getLanguage();

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	public static final String BASE_URL = "http://geofon.gfz-potsdam.de";
	public static final String CFG_KEY_LANGUAGE = "language";
	public static final String CFG_FILE_NAME = "earthquake-bulletin.cfg";
	public static final Configuration configuration;

	private EarthquakeBulletin() {
		throw new IllegalAccessError();
	}

	static {
		final String parent = Messages.get("msg.application.name");
		final File config = new File((parent != null ? parent : "") + File.separator + CFG_FILE_NAME);
		configuration = new Configuration(config.getPath(), true) {
			@Override
			protected void load() {
				super.load();
				final String language = getString(CFG_KEY_LANGUAGE, Defaults.LANGUAGE);
				Messages.setLanguage(language);
				JFaceMessages.setLanguage(language);
			}
		};
	}

	public static void main(final String[] args) {
		EarthquakeBulletinGui.run();
	}

}
