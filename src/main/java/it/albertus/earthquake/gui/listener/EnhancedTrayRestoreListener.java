package it.albertus.earthquake.gui.listener;

import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_MAXIMIZED;
import static it.albertus.earthquake.gui.EarthquakeBulletinGui.START_MINIMIZED;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.EarthquakeBulletinGui.Defaults;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.util.Configuration;

public class EnhancedTrayRestoreListener extends TrayRestoreListener {

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private boolean firstTime = true;

	public EnhancedTrayRestoreListener(final Shell shell, final TrayItem trayItem) {
		super(shell, trayItem);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (firstTime && !getShell().isDisposed() && configuration.getBoolean(START_MINIMIZED, Defaults.START_MINIMIZED) && configuration.getBoolean(SHELL_MAXIMIZED, Defaults.SHELL_MAXIMIZED)) {
			firstTime = false;
			getShell().setMaximized(true);
		}
		super.widgetSelected(e);
	}

}
