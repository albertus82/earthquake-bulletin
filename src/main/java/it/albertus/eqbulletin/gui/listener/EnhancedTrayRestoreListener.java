package it.albertus.eqbulletin.gui.listener;

import static it.albertus.eqbulletin.gui.EarthquakeBulletinGui.SHELL_MAXIMIZED;

import java.util.logging.Level;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.TrayIcon;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.extern.java.Log;

@Log
public class EnhancedTrayRestoreListener extends TrayRestoreListener {

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private boolean firstTime = true;

	public EnhancedTrayRestoreListener(final Shell shell, final TrayItem trayItem) {
		super(shell, trayItem);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (!getShell().isDisposed()) {
			getShell().setMinimized(false);
			if (firstTime && configuration.getBoolean(Preference.MINIMIZE_TRAY, TrayIcon.Defaults.MINIMIZE_TRAY) && configuration.getBoolean(Preference.START_MINIMIZED, EarthquakeBulletinGui.Defaults.START_MINIMIZED) && configuration.getBoolean(SHELL_MAXIMIZED, EarthquakeBulletinGui.Defaults.SHELL_MAXIMIZED)) {
				firstTime = false;
				getShell().setMaximized(true);
				log.log(Level.FINE, "{0}", e);
			}
			if (Util.isGtk()) {
				getShell().setVisible(true);
			}
		}
		super.widgetSelected(e);
		if (Boolean.parseBoolean(String.valueOf(getTrayItem().getData()))) {
			getShell().setMaximized(true);
		}
	}

}
