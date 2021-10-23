package io.github.albertus82.eqbulletin.gui.listener;

import static io.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui.SHELL_MAXIMIZED;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import io.github.albertus82.eqbulletin.gui.TrayIcon;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.albertus82.jface.listener.TrayRestoreListener;
import io.github.albertus82.jface.preference.IPreferencesConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnhancedTrayRestoreListener extends TrayRestoreListener {

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private boolean firstTime = true;

	public EnhancedTrayRestoreListener(final Shell shell, final TrayItem trayItem) {
		super(shell, trayItem);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final Shell shell = getShell();
		if (!shell.isDisposed()) {
			shell.setMinimized(false);
			if (firstTime && configuration.getBoolean(Preference.MINIMIZE_TRAY, TrayIcon.Defaults.MINIMIZE_TRAY) && configuration.getBoolean(Preference.START_MINIMIZED, EarthquakeBulletinGui.Defaults.START_MINIMIZED) && configuration.getBoolean(SHELL_MAXIMIZED, EarthquakeBulletinGui.Defaults.SHELL_MAXIMIZED)) {
				firstTime = false;
				shell.setMaximized(true);
				log.debug("{}", e);
			}
			if (Util.isGtk()) {
				shell.setVisible(true);
			}
		}
		super.widgetSelected(e);
		if (Boolean.parseBoolean(String.valueOf(shell.getData(TrayIcon.SHELL_WAS_MAXIMIZED)))) {
			shell.setMaximized(true);
		}
	}

}
