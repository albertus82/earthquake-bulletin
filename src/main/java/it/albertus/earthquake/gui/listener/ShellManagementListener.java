package it.albertus.earthquake.gui.listener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.util.Configuration;

public class ShellManagementListener extends TrayRestoreListener {

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private boolean firstTime = true;

	public ShellManagementListener(final Shell shell, final TrayItem trayItem) {
		super(shell, trayItem);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (firstTime && !getShell().isDisposed() && configuration.getBoolean("shell.maximized", false)) {
			firstTime = false;
			getShell().setMaximized(true);
		}
		super.widgetSelected(e);
	}

}
