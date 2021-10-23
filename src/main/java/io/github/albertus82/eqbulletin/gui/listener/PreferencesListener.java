package io.github.albertus82.eqbulletin.gui.listener;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.config.TimeZoneConfigAccessor;
import io.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import io.github.albertus82.eqbulletin.gui.Images;
import io.github.albertus82.eqbulletin.gui.MapCanvas;
import io.github.albertus82.eqbulletin.gui.ResultsTable;
import io.github.albertus82.eqbulletin.gui.preference.PageDefinition;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.eqbulletin.resources.Messages.Language;
import io.github.albertus82.jface.EnhancedErrorDialog;
import io.github.albertus82.jface.preference.IPreferencesConfiguration;
import io.github.albertus82.jface.preference.Preferences;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PreferencesListener extends SelectionAdapter implements Listener {

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	@NonNull
	private final EarthquakeBulletinGui gui;

	@Override
	public void handleEvent(final Event event) {
		final Language language = Messages.getLanguage();
		final String timezone = configuration.getString(Preference.TIMEZONE, TimeZoneConfigAccessor.DEFAULT_ZONE_ID);
		final float magnitudeBig = configuration.getFloat(Preference.MAGNITUDE_BIG, ResultsTable.Defaults.MAGNITUDE_BIG);
		final float magnitudeXxl = configuration.getFloat(Preference.MAGNITUDE_XXL, ResultsTable.Defaults.MAGNITUDE_XXL);
		final short mapZoomLevel = configuration.getShort(Preference.MAP_ZOOM_LEVEL, MapCanvas.Defaults.MAP_ZOOM_LEVEL);
		final boolean mapResizeHq = configuration.getBoolean(Preference.MAP_RESIZE_HQ, MapCanvas.Defaults.MAP_RESIZE_HQ);

		final Preferences preferences = new Preferences(PageDefinition.values(), Preference.values(), configuration, Images.getAppIconArray());
		final Shell shell = gui.getShell();
		try {
			preferences.openDialog(shell);
		}
		catch (final IOException e) {
			log.warn("Cannot open Preferences dialog:", e);
			EnhancedErrorDialog.openError(shell, EarthquakeBulletinGui.getApplicationName(), Messages.get("error.preferences.dialog.open"), IStatus.WARNING, e, Images.getAppIconArray());
		}

		// Change language if requested
		if (!language.equals(Messages.getLanguage())) {
			log.debug("Executing language change: {} -> {}.", language, Messages.getLanguage());
			gui.updateLanguage();
		}

		// Refresh results table if needed
		if (!timezone.equals(configuration.getString(Preference.TIMEZONE, TimeZoneConfigAccessor.DEFAULT_ZONE_ID)) || magnitudeBig != configuration.getFloat(Preference.MAGNITUDE_BIG, ResultsTable.Defaults.MAGNITUDE_BIG) || magnitudeXxl != configuration.getFloat(Preference.MAGNITUDE_XXL, ResultsTable.Defaults.MAGNITUDE_XXL)) {
			log.debug("Executing Results table refresh.");
			gui.getResultsTable().getTableViewer().refresh();
		}

		// Refresh status bar if needed
		if (!timezone.equals(configuration.getString(Preference.TIMEZONE, TimeZoneConfigAccessor.DEFAULT_ZONE_ID))) {
			log.debug("Executing Status bar refresh.");
			gui.getStatusBar().refresh();
		}

		// Refresh map if needed
		final short newZoomLevel = configuration.getShort(Preference.MAP_ZOOM_LEVEL, MapCanvas.Defaults.MAP_ZOOM_LEVEL);
		if (mapZoomLevel != newZoomLevel) {
			log.debug("Changing map zoom level: {} -> {}.", mapZoomLevel, newZoomLevel);
			gui.getMapCanvas().setZoomLevel(newZoomLevel);
		}
		else if (mapResizeHq != configuration.getBoolean(Preference.MAP_RESIZE_HQ, MapCanvas.Defaults.MAP_RESIZE_HQ)) {
			log.debug("Executing Map canvas refresh.");
			gui.getMapCanvas().refresh();
		}

		if (preferences.isRestartRequired()) {
			final MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
			messageBox.setText(EarthquakeBulletinGui.getApplicationName());
			messageBox.setMessage(Messages.get("label.preferences.restart"));
			messageBox.open();
		}
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		handleEvent(null);
	}

}
