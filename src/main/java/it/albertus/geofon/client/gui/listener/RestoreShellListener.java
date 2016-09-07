package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.GeofonClientGui;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class RestoreShellListener implements Listener {

	private final GeofonClientGui gui;

	public RestoreShellListener(final GeofonClientGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(Event event) {
		gui.getShell().setVisible(true);
		gui.getTrayIcon().getTrayItem().setVisible(false);
	}

}
