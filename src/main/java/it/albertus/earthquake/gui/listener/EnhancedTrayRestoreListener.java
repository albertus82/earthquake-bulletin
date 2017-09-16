package it.albertus.earthquake.gui.listener;

import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_MAXIMIZED;
import static it.albertus.earthquake.gui.EarthquakeBulletinGui.START_MINIMIZED;
import static it.albertus.earthquake.gui.TrayIcon.MINIMIZE_TRAY;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import it.albertus.earthquake.config.EarthquakeBulletinConfig;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.TrayIcon;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;

public class EnhancedTrayRestoreListener extends TrayRestoreListener {

	private static final Logger logger = LoggerFactory.getLogger(EnhancedTrayRestoreListener.class);

	private static final Configuration configuration = EarthquakeBulletinConfig.getInstance();

	private boolean firstTime = true;

	public EnhancedTrayRestoreListener(final Shell shell, final TrayItem trayItem) {
		super(shell, trayItem);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (firstTime && !getShell().isDisposed() && configuration.getBoolean(MINIMIZE_TRAY, TrayIcon.Defaults.MINIMIZE_TRAY) && configuration.getBoolean(START_MINIMIZED, EarthquakeBulletinGui.Defaults.START_MINIMIZED) && configuration.getBoolean(SHELL_MAXIMIZED, EarthquakeBulletinGui.Defaults.SHELL_MAXIMIZED)) {
			firstTime = false;
			getShell().setMaximized(true);
			logger.log(Level.FINE, "{0}", e);
		}
		super.widgetSelected(e);
	}

}
