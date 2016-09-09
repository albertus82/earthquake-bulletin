package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.GeofonClient;
import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.preference.PageDefinition;
import it.albertus.geofon.client.gui.preference.Preference;
import it.albertus.geofon.client.resources.Messages;
import it.albertus.geofon.client.resources.Messages.Language;
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.preference.Preferences;
import it.albertus.util.Configuration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.MessageBox;

public class PreferencesSelectionListener extends SelectionAdapter {

	private final Configuration configuration = GeofonClient.configuration;
	private final GeofonClientGui gui;

	public PreferencesSelectionListener(final GeofonClientGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Language language = Messages.getLanguage();
		final Preferences preferences = new Preferences(PageDefinition.values(), Preference.values(), configuration, gui.getFavicon() != null ? new Image[] { gui.getFavicon() } : null);
		try {
			preferences.openDialog(gui.getShell());
		}
		catch (final Exception e) {
			e.printStackTrace();
		}

		// Update language...
		final String updatedLanguageCode = configuration.getString("language", GeofonClient.Defaults.LANGUAGE);
		Messages.setLanguage(updatedLanguageCode);
		JFaceMessages.setLanguage(updatedLanguageCode);

		// Check if must update texts...
		if (gui != null && !language.equals(Messages.getLanguage())) {
			gui.getMenuBar().updateTexts();
			gui.getResultTable().updateTexts();
		}
		if (preferences.isRestartRequired()) {
			final MessageBox messageBox = new MessageBox(gui.getShell(), SWT.ICON_INFORMATION);
			messageBox.setText(Messages.get("lbl.window.title"));
			messageBox.setMessage(Messages.get("lbl.preferences.restart"));
			messageBox.open();
		}
	}

}
