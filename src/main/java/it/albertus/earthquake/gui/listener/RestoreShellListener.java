package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class RestoreShellListener implements Listener {

	private final EarthquakeBulletinGui gui;

	public RestoreShellListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(Event event) {
		gui.getShell().setVisible(true);
		gui.getTrayIcon().getTrayItem().setVisible(false);
	}

}
