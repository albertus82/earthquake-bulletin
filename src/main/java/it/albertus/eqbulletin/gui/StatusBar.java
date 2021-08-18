package it.albertus.eqbulletin.gui;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.eqbulletin.config.TimeZoneConfigAccessor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;
import it.albertus.jface.i18n.LocalizedWidgets;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class StatusBar implements Multilanguage {

	private static final String SPACER = "      ";

	private final StatusLineManager manager;

	private TemporalAccessor lastUpdateTime;
	private int itemCount;

	private final LocalizedWidgets localizedWidgets = new LocalizedWidgets();

	StatusBar(@NonNull final StatusLineManager manager) {
		this.manager = manager;
		try {
			localizedWidgets.put(getCopyMenuItem(), () -> Messages.get("label.menu.item.copy"));
		}
		catch (final Exception e) {
			log.warn("Cannot localize the status bar:", e);
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
			final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Messages.getLanguage().getLocale()).withZone(TimeZoneConfigAccessor.getZoneId());
			message.append(SPACER);
			message.append(Messages.get("label.status.bar.last.updated", dateTimeFormatter.format(lastUpdateTime)));
		}
		manager.setMessage(message.toString());
	}

	private MenuItem getCopyMenuItem() throws IllegalAccessException, NoSuchFieldException {
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
