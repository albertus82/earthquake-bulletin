package it.albertus.eqbulletin.gui;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.Multilanguage;

public class StatusBar implements IShellProvider, Multilanguage {

	private final StatusLineManager manager;
	private final Shell shell;

	private TemporalAccessor lastUpdatedTime;

	StatusBar(final EarthquakeBulletinGui gui) {
		shell = gui.getShell();
		gui.createStatusLine(shell);
		manager = gui.getStatusLineManager();
		GridDataFactory.fillDefaults().grab(true, false).applyTo(manager.getControl());
	}

	public void setLastUpdateTime(final TemporalAccessor dateTime) {
		this.lastUpdatedTime = dateTime;
		refreshMessage();
	}

	private ZoneId getZoneId() {
		return ZoneId.of(EarthquakeBulletinConfig.getInstance().getString(Preference.TIMEZONE, EarthquakeBulletin.Defaults.TIME_ZONE_ID));
	}

	private Locale getLocale() {
		return Messages.getLanguage().getLocale();
	}

	void refreshMessage() {
		manager.setMessage(Messages.get("lbl.status.bar.last.updated", DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(getLocale()).withZone(getZoneId()).format(lastUpdatedTime)));
	}

	@Override
	public void updateLanguage() {
		refreshMessage();
	}

	@Override
	public Shell getShell() {
		return shell;
	}

}
