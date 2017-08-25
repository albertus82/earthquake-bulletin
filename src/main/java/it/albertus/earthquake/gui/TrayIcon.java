package it.albertus.earthquake.gui;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.listener.CloseListener;
import it.albertus.earthquake.gui.listener.EnhancedTrayRestoreListener;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.util.Configuration;
import it.albertus.util.NewLine;
import it.albertus.util.logging.LoggerFactory;

public class TrayIcon {

	private static final Logger logger = LoggerFactory.getLogger(TrayIcon.class);

	public static class Defaults {
		public static final boolean MINIMIZE_TRAY = true;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	public static final String MINIMIZE_TRAY = "minimize.tray";

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private final EarthquakeBulletinGui gui;

	private Tray tray;
	private TrayItem trayItem;

	private final Map<Integer, ToolTip> toolTips = new HashMap<>(4);
	private Menu trayMenu;
	private MenuItem showMenuItem;
	private MenuItem exitMenuItem;

	/* To be accessed only from this class */
	private Image trayIcon;

	public TrayIcon(final EarthquakeBulletinGui gui) {
		this.gui = gui;
		gui.getShell().addShellListener(new ShellAdapter() {
			@Override
			public void shellIconified(final ShellEvent se) {
				if (configuration.getBoolean(MINIMIZE_TRAY, Defaults.MINIMIZE_TRAY)) {
					iconify();
				}
			}
		});
	}

	private Image getTrayIcon() {
		return Images.getMainIcons()[1];
	}

	private void iconify() {
		if (tray == null || trayItem == null || trayItem.isDisposed()) {
			/* Inizializzazione */
			try {
				tray = gui.getShell().getDisplay().getSystemTray();

				if (tray != null) {
					trayItem = new TrayItem(tray, SWT.NONE);
					trayIcon = getTrayIcon();
					trayItem.setImage(trayIcon);
					trayItem.setToolTipText(Messages.get("lbl.tray.tooltip"));
					final TrayRestoreListener trayRestoreListener = new EnhancedTrayRestoreListener(gui.getShell(), trayItem);

					for (final int icon : new int[] { SWT.ICON_INFORMATION, SWT.ICON_WARNING, SWT.ICON_ERROR }) {
						final ToolTip toolTip = new ToolTip(gui.getShell(), SWT.BALLOON | icon);
						toolTip.setVisible(false);
						toolTip.setAutoHide(true);
						toolTip.addSelectionListener(trayRestoreListener);
						toolTips.put(icon, toolTip);
					}

					trayMenu = new Menu(gui.getShell(), SWT.POP_UP);
					showMenuItem = new MenuItem(trayMenu, SWT.PUSH);
					showMenuItem.setText(Messages.get("lbl.tray.show"));
					showMenuItem.addSelectionListener(trayRestoreListener);
					trayMenu.setDefaultItem(showMenuItem);

					new MenuItem(trayMenu, SWT.SEPARATOR);

					exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
					exitMenuItem.setText(Messages.get("lbl.tray.close"));
					exitMenuItem.addSelectionListener(new CloseListener(gui));
					trayItem.addMenuDetectListener(new MenuDetectListener() {
						@Override
						public void menuDetected(MenuDetectEvent e) {
							trayMenu.setVisible(true);
						}
					});

					trayItem.addSelectionListener(trayRestoreListener);
					if (!Util.isLinux()) {
						gui.getShell().addShellListener(trayRestoreListener);
					}
				}
			}
			catch (final Exception e) {
				logger.log(Level.SEVERE, Messages.get("err.tray.init"), e);
			}
		}

		if (tray != null && !tray.isDisposed() && trayItem != null && !trayItem.isDisposed()) {
			gui.getShell().setVisible(false);
			trayItem.setVisible(true);
			trayItem.setImage(trayIcon); // Update icon
			gui.getShell().setMinimized(false);
		}
	}

	public void showBalloonToolTip(final Earthquake earthquake) {
		if (trayItem != null && !trayItem.isDisposed()) {
			final StringBuilder text = new StringBuilder();
			text.append("M ").append(earthquake.getMagnitude()).append(", ").append(earthquake.getRegion());

			final StringBuilder message = new StringBuilder();
			final DateFormat df = ResultsTable.dateFormat.get();
			df.setTimeZone(TimeZone.getTimeZone(configuration.getString("timezone", EarthquakeBulletin.Defaults.TIME_ZONE_ID)));
			message.append(df.format(earthquake.getTime())).append(' ');
			message.append(earthquake.getLatitude()).append(' ');
			message.append(earthquake.getLongitude()).append(' ');
			message.append(earthquake.getDepth()).append(' ');
			message.append(earthquake.getStatus());

			final ToolTip toolTip;
			if (earthquake.getMagnitude() >= configuration.getFloat("magnitude.xxl", ResultsTable.Defaults.MAGNITUDE_XXL)) {
				toolTip = toolTips.get(SWT.ICON_ERROR);
			}
			else if (earthquake.getMagnitude() >= configuration.getFloat("magnitude.big", ResultsTable.Defaults.MAGNITUDE_BIG)) {
				toolTip = toolTips.get(SWT.ICON_WARNING);
			}
			else {
				toolTip = toolTips.get(SWT.ICON_INFORMATION);
			}

			try {
				trayItem.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						logger.log(Level.FINE, "{0}", text);
						trayItem.setToolTip(toolTip);
						toolTip.setText(text.toString().trim());
						toolTip.setMessage(message.toString().trim());
						toolTip.setVisible(true);
					}
				});
			}
			catch (final RuntimeException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
	}

	public void updateToolTipText(final Earthquake earthquake) {
		if (trayItem != null && !trayItem.isDisposed()) {
			final StringBuilder buf = new StringBuilder(Messages.get("lbl.tray.tooltip"));
			if (earthquake != null) {
				buf.append(NewLine.SYSTEM_LINE_SEPARATOR);
				buf.append("M ").append(earthquake.getMagnitude()).append(", ").append(earthquake.getRegion());
				buf.append(NewLine.SYSTEM_LINE_SEPARATOR);
				final DateFormat df = ResultsTable.dateFormat.get();
				df.setTimeZone(TimeZone.getTimeZone(configuration.getString("timezone", EarthquakeBulletin.Defaults.TIME_ZONE_ID)));
				buf.append(df.format(earthquake.getTime())).append(' ');
				buf.append(earthquake.getLatitude()).append(' ');
				buf.append(earthquake.getLongitude()).append(' ');
				buf.append(earthquake.getDepth()).append(' ');
				buf.append(earthquake.getStatus());
			}
			final String text = buf.toString();

			try {
				trayItem.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						if (!text.equals(trayItem.getToolTipText())) {
							trayItem.setToolTipText(text);
						}
					}
				});
			}
			catch (final RuntimeException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
	}

	public void updateTexts() {
		if (trayMenu != null) {
			showMenuItem.setText(Messages.get("lbl.tray.show"));
			exitMenuItem.setText(Messages.get("lbl.tray.close"));
		}
	}

	public TrayItem getTrayItem() {
		return trayItem;
	}

}
