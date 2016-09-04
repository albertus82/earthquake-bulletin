package it.albertus.geofon.client;

import it.albertus.geofon.client.gui.GeofonClientGui;

import org.eclipse.swt.widgets.Display;

public class GeofonClient {

	public static void main(String[] args) {
		final Display display = Display.getDefault();
		new GeofonClientGui(display);
		display.dispose();
	}

}
