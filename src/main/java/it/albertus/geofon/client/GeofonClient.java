package it.albertus.geofon.client;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.util.Configuration;

public class GeofonClient {

	public static final Configuration configuration = new Configuration("geofonclient.cfg");

	public static void main(final String[] args) {
		GeofonClientGui.run();
	}

}
