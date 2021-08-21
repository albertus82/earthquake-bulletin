package it.albertus.eqbulletin.gui;

import java.awt.SystemTray;
import java.util.Map;

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
import it.albertus.eqbulletin.config.TimeZoneConfigAccessor;
import it.albertus.eqbulletin.gui.listener.CloseListener;
import it.albertus.eqbulletin.gui.listener.EnhancedTrayRestoreListener;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.i18n.LocalizedWidgets;
import it.albertus.jface.listener.TrayRestoreListener;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.MapUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrayIcon implements IShellProvider, Multilanguage {

	public static final String SHELL_WAS_MAXIMIZED = EarthquakeBulletinGui.class.getName() + ".wasMaximizedWhenMinimizedOnTray";

	private static final int[] icons = { SWT.ICON_INFORMATION, SWT.ICON_WARNING, SWT.ICON_ERROR };

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final boolean MINIMIZE_TRAY = SystemTray.isSupported();
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	@Getter
	private final Shell shell;

	@Getter
	private TrayItem trayItem;

	private final Map<Integer, ToolTip> tooltips = MapUtils.newHashMapWithExpectedSize(icons.length);

	private final LocalizedWidgets localizedWidgets = new LocalizedWidgets();

	// To be accessed only from this class
	private Image image;
	private String tooltipText = EarthquakeBulletinGui.getApplicationName();

	TrayIcon(@NonNull final EarthquakeBulletinGui gui) {
		shell = gui.getShell();
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellIconified(final ShellEvent se) {
				if (configuration.getBoolean(Preference.MINIMIZE_TRAY, Defaults.MINIMIZE_TRAY)) {
					if (SystemTray.isSupported()) {
						iconify(gui);
					}
					else {
						log.info("The system tray is not supported on the current platform.");
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
		if (trayItem == null || trayItem.isDisposed()) {
			log.debug("Initializing tray item...");
			try {
				final Tray tray = shell.getDisplay().getSystemTray();
				if (tray != null) {
					trayItem = new TrayItem(tray, SWT.NONE);
					image = getImage();
					trayItem.setImage(image);
					trayItem.setToolTipText(tooltipText);
					final TrayRestoreListener trayRestoreListener = new EnhancedTrayRestoreListener(shell, trayItem);

					for (final int icon : icons) {
						final ToolTip tooltip = new ToolTip(shell, SWT.BALLOON | icon);
						tooltip.setVisible(false);
						tooltip.setAutoHide(true);
						tooltip.addSelectionListener(trayRestoreListener);
						tooltips.put(icon, tooltip);
					}

					final Menu trayMenu = new Menu(shell, SWT.POP_UP);
					final MenuItem showMenuItem = newLocalizedMenuItem(trayMenu, SWT.PUSH, "label.tray.show");
					showMenuItem.addSelectionListener(trayRestoreListener);
					trayMenu.setDefaultItem(showMenuItem);

					new MenuItem(trayMenu, SWT.SEPARATOR);

					final MenuItem exitMenuItem = newLocalizedMenuItem(trayMenu, SWT.PUSH, "label.tray.close");
					exitMenuItem.addSelectionListener(new CloseListener(gui));
					trayItem.addMenuDetectListener(e -> trayMenu.setVisible(true));

					trayItem.addSelectionListener(trayRestoreListener);
					if (!Util.isLinux()) {
						shell.addShellListener(trayRestoreListener);
					}
				}
				log.debug("Tray item initialized successfully.");
			}
			catch (final Exception e) {
				log.error("Cannot initialize tray icon:", e);
			}
		}

		if (trayItem != null && !trayItem.isDisposed()) {
			shell.setData(SHELL_WAS_MAXIMIZED, shell.getMaximized());
			shell.setVisible(false);
			trayItem.setVisible(true);
			trayItem.setImage(image); // Update icon
		}
	}

	public void showBalloonTooltip(@NonNull final Earthquake earthquake) {
		if (trayItem != null && !trayItem.isDisposed()) {
			final ToolTip tooltip;
			if (earthquake.getMagnitude() >= configuration.getFloat(Preference.MAGNITUDE_XXL, ResultsTable.Defaults.MAGNITUDE_XXL)) {
				tooltip = tooltips.get(SWT.ICON_ERROR);
			}
			else if (earthquake.getMagnitude() >= configuration.getFloat(Preference.MAGNITUDE_BIG, ResultsTable.Defaults.MAGNITUDE_BIG)) {
				tooltip = tooltips.get(SWT.ICON_WARNING);
			}
			else {
				tooltip = tooltips.get(SWT.ICON_INFORMATION);
			}

			try {
				trayItem.getDisplay().syncExec(() -> {
					trayItem.setToolTip(tooltip);
					tooltip.setText(earthquake.getSummary());
					tooltip.setMessage(earthquake.getDetails(TimeZoneConfigAccessor.getZoneId()));
					tooltip.setVisible(true);
				});
			}
			catch (final RuntimeException e) {
				log.warn("Cannot show balloon tooltip for " + earthquake + ':', e);
			}
		}
	}

	public void updateTooltipText(final Earthquake earthquake) {
		final StringBuilder buf = new StringBuilder(EarthquakeBulletinGui.getApplicationName());
		if (earthquake != null) {
			buf.append(System.lineSeparator());
			buf.append(earthquake.getSummary());
			buf.append(System.lineSeparator());
			buf.append(earthquake.getDetails(TimeZoneConfigAccessor.getZoneId()));
		}
		tooltipText = buf.toString();
		if (trayItem != null && !trayItem.isDisposed()) {
			try {
				trayItem.getDisplay().syncExec(() -> {
					if (!tooltipText.equals(trayItem.getToolTipText())) {
						trayItem.setToolTipText(tooltipText);
					}
				});
			}
			catch (final RuntimeException e) {
				log.warn("Cannot update tooltip text for " + earthquake + ":", e);
			}
		}
	}

	@Override
	public void updateLanguage() {
		localizedWidgets.resetAllTexts();
	}

	private MenuItem newLocalizedMenuItem(@NonNull final Menu parent, final int style, @NonNull final String messageKey) {
		return localizedWidgets.putAndReturn(new MenuItem(parent, style), () -> Messages.get(messageKey)).getKey();
	}

}
