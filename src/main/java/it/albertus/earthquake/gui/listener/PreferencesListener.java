package it.albertus.earthquake.gui.listener;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.preference.PageDefinition;
import it.albertus.earthquake.gui.preference.Preference;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.resources.Messages.Language;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.preference.Preferences;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;

public class PreferencesListener extends SelectionAdapter implements Listener {

	private static final Logger logger = LoggerFactory.getLogger(PreferencesListener.class);

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private final EarthquakeBulletinGui gui;

	public PreferencesListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Language language = Messages.getLanguage();
		final String timezone = configuration.getString("timezone", EarthquakeBulletin.Defaults.TIME_ZONE_ID);
		final Preferences preferences = new Preferences(PageDefinition.values(), Preference.values(), configuration, Images.getMainIcons());
		final Shell shell = gui.getShell();
		try {
			preferences.openDialog(shell);
		}
		catch (final IOException ioe) {
			final String message = Messages.get("err.preferences.dialog.open");
			logger.log(Level.WARNING, message, ioe);
			EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), message, IStatus.WARNING, ioe, Images.getMainIcons());
		}

		// Check if must update texts...
		if (!language.equals(Messages.getLanguage())) {
			gui.updateLanguage();
		}

		// Check if time zone has changed...
		if (!timezone.equals(configuration.getString("timezone", EarthquakeBulletin.Defaults.TIME_ZONE_ID))) {
			gui.getResultsTable().getTableViewer().refresh();
		}

		if (preferences.isRestartRequired()) {
			final MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
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
