package it.albertus.eqbulletin.gui;

import java.lang.reflect.Field;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.logging.LoggerFactory;

public class StatusBar implements IShellProvider, Multilanguage {

	private static final Logger logger = LoggerFactory.getLogger(StatusBar.class);

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private final StatusLineManager manager;
	private final Shell shell;

	private TemporalAccessor lastUpdatedTime;

	StatusBar(final EarthquakeBulletinGui gui) {
		shell = gui.getShell();
		gui.createStatusLine(shell);
		manager = gui.getStatusLineManager();
		localizeContextMenu(manager);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(manager.getControl());
	}

	@Override
	public Shell getShell() {
		return shell;
	}

	@Override
	public void updateLanguage() {
		refreshMessage();
		localizeContextMenu(manager);
	}

	public void setLastUpdateTime(final TemporalAccessor dateTime) {
		this.lastUpdatedTime = dateTime;
		refreshMessage();
	}

	void refreshMessage() {
		manager.setMessage(Messages.get("lbl.status.bar.last.updated", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(getLocale()).withZone(getZoneId()).format(lastUpdatedTime)));
	}

	private static ZoneId getZoneId() {
		try {
			return ZoneId.of(configuration.getString(Preference.TIMEZONE, EarthquakeBulletin.Defaults.TIME_ZONE_ID));
		}
		catch (final DateTimeException e) {
			logger.log(Level.WARNING, e.toString(), e);
			return ZoneId.of(EarthquakeBulletin.Defaults.TIME_ZONE_ID);
		}
	}

	private static Locale getLocale() {
		return Messages.getLanguage().getLocale();
	}

	private static void localizeContextMenu(final StatusLineManager manager) {
		try {
			for (final Field f1 : manager.getClass().getDeclaredFields()) {
				if (f1.getType().isAssignableFrom(Composite.class)) {
					f1.setAccessible(true);
					final Composite statusLine = (Composite) f1.get(manager);
					for (final Field f2 : statusLine.getClass().getDeclaredFields()) {
						if ("copyMenuItem".equalsIgnoreCase(f2.getName()) && f2.getType().isAssignableFrom(MenuItem.class)) {
							f2.setAccessible(true);
							final MenuItem copyMenuItem = (MenuItem) f2.get(statusLine);
							copyMenuItem.setText(Messages.get("lbl.menu.item.copy"));
						}
					}
				}
			}
		}
		catch (final IllegalAccessException | RuntimeException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

}
