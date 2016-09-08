package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.GeofonClient;
import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.preference.PageDefinition;
import it.albertus.geofon.client.gui.preference.Preference;
import it.albertus.jface.preference.Preferences;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;

public class PreferencesSelectionListener extends SelectionAdapter {

	private final GeofonClientGui gui;

	public PreferencesSelectionListener(final GeofonClientGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Preferences preferences = new Preferences(PageDefinition.values(), Preference.values(), GeofonClient.configuration, new Image[] {gui.getFavicon()});
		try {
			preferences.openDialog(gui.getShell());
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
//		if (preferences.isRestartRequired()) {
//			final MessageBox messageBox = new MessageBox(gui.getShell(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
//			messageBox.setText(Messages.get("lbl.window.title"));
//			messageBox.setMessage(Messages.get("lbl.preferences.restart"));
//			if (messageBox.open() == SWT.YES) {
//				gui.restart();
//			}
//		}
	}

}
