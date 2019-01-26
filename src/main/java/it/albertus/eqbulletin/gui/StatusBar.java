package it.albertus.eqbulletin.gui;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.layout.GridDataFactory;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.resources.Messages;

public class StatusBar {

	private final StatusLineManager manager;

	private TemporalAccessor lastUpdatedTime;

	public StatusBar(final EarthquakeBulletinGui gui) {
		gui.createStatusLine(gui.getShell());
		this.manager = gui.getStatusLineManager();
		GridDataFactory.fillDefaults().grab(true, false).applyTo(manager.getControl());
	}

	public TemporalAccessor getDateTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdateTime(final TemporalAccessor dateTime) {
		this.lastUpdatedTime = dateTime;
		refreshMessage();
	}

	public ZoneId getZoneId() {
		return ZoneId.of(EarthquakeBulletinConfig.getInstance().getString(Preference.TIMEZONE, EarthquakeBulletin.Defaults.TIME_ZONE_ID));
	}

	public Locale getLocale() {
		return Messages.getLanguage().getLocale();
	}

	public void refreshMessage() {
		manager.setMessage(Messages.get("lbl.status.bar.last.updated", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(getLocale()).withZone(getZoneId()).format(lastUpdatedTime)));
	}

	public void updateTexts() {
		refreshMessage();
	}

}
