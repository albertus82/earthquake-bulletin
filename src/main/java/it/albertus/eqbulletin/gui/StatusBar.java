package it.albertus.eqbulletin.gui;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.logging.Level;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.config.TimeZoneConfig;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.i18n.LocalizedWidgets;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
public class StatusBar implements IShellProvider, Multilanguage {

	private static final String SPACER = "      ";

	private final StatusLineManager manager;
	@Getter private final Shell shell;

	@Setter private TemporalAccessor lastUpdateTime;
	@Setter private int itemCount;

	private final LocalizedWidgets localizedWidgets = new LocalizedWidgets();

	StatusBar(@NonNull final EarthquakeBulletinGui gui) {
		shell = gui.getShell();
		gui.createStatusLine(shell);
		manager = gui.getStatusLineManager();
		try {
			localizedWidgets.put(getCopyMenuItem(manager), () -> Messages.get("label.menu.item.copy"));
		}
		catch (final Exception e) {
			log.log(Level.WARNING, "Cannot localize the status bar:", e);
		}
		GridDataFactory.fillDefaults().grab(true, false).applyTo(manager.getControl());
		refresh();
	}

	@Override
	public void updateLanguage() {
		refresh();
		localizedWidgets.resetAllTexts();
	}

	public void refresh() {
		final StringBuilder message = new StringBuilder(Messages.get(itemCount == 1 ? "label.status.bar.item.count" : "label.status.bar.items.count", itemCount));
		if (lastUpdateTime != null) {
			final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(getLocale()).withZone(TimeZoneConfig.getZoneId());
			message.append(SPACER);
			message.append(Messages.get("label.status.bar.last.updated", dateTimeFormatter.format(lastUpdateTime)));
		}
		manager.setMessage(message.toString());
	}

	private static Locale getLocale() {
		return Messages.getLanguage().getLocale();
	}

	private static MenuItem getCopyMenuItem(@NonNull final StatusLineManager manager) throws IllegalAccessException, NoSuchFieldException {
		final Control statusLine = manager.getControl();
		for (final Field field : statusLine.getClass().getDeclaredFields()) {
			if (field.getName().toLowerCase(Locale.ROOT).contains("copy") && MenuItem.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				return (MenuItem) field.get(statusLine);
			}
		}
		throw new NoSuchFieldException();
	}

}
