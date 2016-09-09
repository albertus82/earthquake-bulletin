package it.albertus.geofon.client;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.util.Configuration;

import java.io.File;

public class GeofonClient {

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
		configuration = new Configuration(config.getPath());
	}

	public static void main(final String[] args) {
		GeofonClientGui.run();
	}

}
