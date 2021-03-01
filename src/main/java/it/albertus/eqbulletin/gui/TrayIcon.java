package it.albertus.eqbulletin.gui;

import java.awt.SystemTray;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.util.Util;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.config.TimeZoneConfig;
import it.albertus.eqbulletin.gui.listener.CloseListener;
import it.albertus.eqbulletin.gui.listener.EnhancedTrayRestoreListener;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.MapUtils;
import it.albertus.util.logging.LoggerFactory;

public class TrayIcon implements IShellProvider, Multilanguage {

	private static final Logger logger = LoggerFactory.getLogger(TrayIcon.class);

	private static final int[] icons = { SWT.ICON_INFORMATION, SWT.ICON_WARNING, SWT.ICON_ERROR };

	public static class Defaults {
		public static final boolean MINIMIZE_TRAY = SystemTray.isSupported();

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private final Shell shell;

	private Tray tray;
	private TrayItem trayItem;

	private final Map<Integer, ToolTip> toolTips = MapUtils.newHashMapWithExpectedSize(icons.length);
	private Menu trayMenu;
	private MenuItem showMenuItem;
	private MenuItem exitMenuItem;

	/* To be accessed only from this class */
	private Image image;
	private String toolTipText = Messages.get("lbl.tray.tooltip");

	TrayIcon(final EarthquakeBulletinGui gui) {
		shell = gui.getShell();
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellIconified(final ShellEvent se) {
				if (configuration.getBoolean(Preference.MINIMIZE_TRAY, Defaults.MINIMIZE_TRAY)) {
					if (SystemTray.isSupported()) {
						iconify(gui);
					}
					else {
						logger.log(Level.INFO, "The system tray is not supported on the current platform.");
					}
				}
			}
		});
	}

	private static Image getImage() {
		final Point point = Display.getDefault().getDPI();
		final int dpi = (point.x + point.y) / 2;
		if (Util.isWindows() && dpi <= 96) {
			return Images.getAppIconMap().get(new Rectangle(0, 0, 16, 16)); // looks slightly better on Windows with no scaling
		}
		else {
			return Images.getAppIconMap().get(new Rectangle(0, 0, 32, 32));
		}
	}

	private void iconify(final EarthquakeBulletinGui gui) {
		if (tray == null || trayItem == null || trayItem.isDisposed()) {
			/* Inizializzazione */
			try {
				tray = shell.getDisplay().getSystemTray();

				if (tray != null) {
					trayItem = new TrayItem(tray, SWT.NONE);
					image = getImage();
					trayItem.setImage(image);
					trayItem.setToolTipText(toolTipText);
					final TrayRestoreListener trayRestoreListener = new EnhancedTrayRestoreListener(shell, trayItem);

					for (final int icon : icons) {
						final ToolTip toolTip = new ToolTip(shell, SWT.BALLOON | icon);
						toolTip.setVisible(false);
						toolTip.setAutoHide(true);
						toolTip.addSelectionListener(trayRestoreListener);
						toolTips.put(icon, toolTip);
					}

					trayMenu = new Menu(shell, SWT.POP_UP);
					showMenuItem = new MenuItem(trayMenu, SWT.PUSH);
					showMenuItem.setText(Messages.get("lbl.tray.show"));
					showMenuItem.addSelectionListener(trayRestoreListener);
					trayMenu.setDefaultItem(showMenuItem);

					new MenuItem(trayMenu, SWT.SEPARATOR);

					exitMenuItem = new MenuItem(trayMenu, SWT.PUSH);
					exitMenuItem.setText(Messages.get("lbl.tray.close"));
					exitMenuItem.addSelectionListener(new CloseListener(gui));
					trayItem.addMenuDetectListener(e -> trayMenu.setVisible(true));

					trayItem.addSelectionListener(trayRestoreListener);
					if (!Util.isLinux()) {
						shell.addShellListener(trayRestoreListener);
					}
				}
			}
			catch (final Exception e) {
				logger.log(Level.SEVERE, Messages.get("err.tray.init"), e);
			}
		}

		if (tray != null && !tray.isDisposed() && trayItem != null && !trayItem.isDisposed()) {
			shell.setVisible(false);
			trayItem.setVisible(true);
			trayItem.setImage(image); // Update icon
			shell.setMinimized(false);
		}
	}

	public void showBalloonToolTip(final Earthquake earthquake) {
		if (trayItem != null && !trayItem.isDisposed()) {
			final ToolTip toolTip;
			if (earthquake.getMagnitude() >= configuration.getFloat(Preference.MAGNITUDE_XXL, ResultsTable.Defaults.MAGNITUDE_XXL)) {
				toolTip = toolTips.get(SWT.ICON_ERROR);
			}
			else if (earthquake.getMagnitude() >= configuration.getFloat(Preference.MAGNITUDE_BIG, ResultsTable.Defaults.MAGNITUDE_BIG)) {
				toolTip = toolTips.get(SWT.ICON_WARNING);
			}
			else {
				toolTip = toolTips.get(SWT.ICON_INFORMATION);
			}

			try {
				trayItem.getDisplay().syncExec(() -> {
					trayItem.setToolTip(toolTip);
					toolTip.setText(earthquake.getSummary());
					toolTip.setMessage(earthquake.getDetails(TimeZoneConfig.getZoneId()));
					toolTip.setVisible(true);
				});
			}
			catch (final RuntimeException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
	}

	public void updateToolTipText(final Earthquake earthquake) {
		final StringBuilder buf = new StringBuilder(Messages.get("lbl.tray.tooltip"));
		if (earthquake != null) {
			buf.append(System.lineSeparator());
			buf.append(earthquake.getSummary());
			buf.append(System.lineSeparator());
			buf.append(earthquake.getDetails(TimeZoneConfig.getZoneId()));
		}
		toolTipText = buf.toString();
		if (trayItem != null && !trayItem.isDisposed()) {
			try {
				trayItem.getDisplay().syncExec(() -> {
					if (!toolTipText.equals(trayItem.getToolTipText())) {
						trayItem.setToolTipText(toolTipText);
					}
				});
			}
			catch (final RuntimeException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
	}

	@Override
	public void updateLanguage() {
		if (trayMenu != null) {
			showMenuItem.setText(Messages.get("lbl.tray.show"));
			exitMenuItem.setText(Messages.get("lbl.tray.close"));
		}
	}

	public TrayItem getTrayItem() {
		return trayItem;
	}

	@Override
	public Shell getShell() {
		return shell;
	}

}
