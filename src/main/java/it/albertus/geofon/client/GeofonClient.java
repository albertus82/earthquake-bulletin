package it.albertus.geofon.client;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.util.Configuration;

import java.io.File;
import java.util.Locale;

public class GeofonClient {

	public interface Defaults {
		String LANGUAGE = Locale.getDefault().getLanguage();
	}

	public static final String CFG_FILE_NAME = "geofon-client.cfg";
	public static final Configuration configuration;

	static {
		File config = null;
		try {
			final String parent = new File(GeofonClient.class.getProtectionDomain().getCodeSource().getLocation().toURI().getSchemeSpecificPart()).getParent();
			config = new File((parent != null ? parent : "") + File.separator + CFG_FILE_NAME);
		}
		catch (final Exception e) {
			config = new File(CFG_FILE_NAME);
		}
		configuration = new Configuration(config.getPath()) {
			@Override
			protected void load() {
				super.load();
				final String language = getString("language", Defaults.LANGUAGE);
				Messages.setLanguage(language);
				JFaceMessages.setLanguage(language);
			};
		};
	}

	public static void main(final String[] args) {
		GeofonClientGui.run();
	}

}
