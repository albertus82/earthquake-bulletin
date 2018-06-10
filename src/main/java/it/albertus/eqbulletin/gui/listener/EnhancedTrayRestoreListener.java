package it.albertus.eqbulletin.gui.listener;

import static it.albertus.eqbulletin.gui.EarthquakeBulletinGui.SHELL_MAXIMIZED;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.TrayIcon;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.logging.LoggerFactory;

public class EnhancedTrayRestoreListener extends TrayRestoreListener {

	private static final Logger logger = LoggerFactory.getLogger(EnhancedTrayRestoreListener.class);

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private boolean firstTime = true;

	public EnhancedTrayRestoreListener(final Shell shell, final TrayItem trayItem) {
		super(shell, trayItem);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (firstTime && !getShell().isDisposed() && configuration.getBoolean(Preference.MINIMIZE_TRAY, TrayIcon.Defaults.MINIMIZE_TRAY) && configuration.getBoolean(Preference.START_MINIMIZED, EarthquakeBulletinGui.Defaults.START_MINIMIZED) && configuration.getBoolean(SHELL_MAXIMIZED, EarthquakeBulletinGui.Defaults.SHELL_MAXIMIZED)) {
			firstTime = false;
			getShell().setMaximized(true);
			logger.log(Level.FINE, "{0}", e);
		}
		super.widgetSelected(e);
	}

}
