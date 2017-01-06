package it.albertus.earthquake.gui.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.preference.PageDefinition;
import it.albertus.earthquake.gui.preference.Preference;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.resources.Messages.Language;
import it.albertus.jface.preference.Preferences;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;

public class PreferencesListener extends SelectionAdapter implements Listener {

	private static final Logger logger = LoggerFactory.getLogger(PreferencesListener.class);

	private static final Configuration configuration = EarthquakeBulletin.configuration;

	private final EarthquakeBulletinGui gui;

	public PreferencesListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Language language = Messages.getLanguage();
		final Preferences preferences = new Preferences(PageDefinition.values(), Preference.values(), configuration, Images.MAIN_ICONS);
		try {
			preferences.openDialog(gui.getShell());
		}
		catch (final Exception e) {
			logger.log(Level.SEVERE, Messages.get("err.preferences.dialog.open"), e);
		}

		// Check if must update texts...
		if (!language.equals(Messages.getLanguage())) {
			gui.getMenuBar().updateTexts();
			gui.getResultsTable().updateTexts();
			gui.getSearchForm().updateTexts();
			gui.getMapCanvas().updateTexts();
			gui.getShell().layout(true, true);
		}
		if (preferences.isRestartRequired()) {
			final MessageBox messageBox = new MessageBox(gui.getShell(), SWT.ICON_INFORMATION);
			messageBox.setText(Messages.get("lbl.window.title"));
			messageBox.setMessage(Messages.get("lbl.preferences.restart"));
			messageBox.open();
		}
	}

	@Override
	public void handleEvent(final Event event) {
		widgetSelected(null);
	}

}
