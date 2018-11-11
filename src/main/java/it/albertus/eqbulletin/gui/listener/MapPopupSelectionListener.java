package it.albertus.eqbulletin.gui.listener;

import java.text.DateFormat;
import java.util.TimeZone;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.maps.MapDialog;
import it.albertus.jface.maps.MapMarker;
import it.albertus.jface.maps.MapOptions;
import it.albertus.jface.maps.leaflet.LeafletMapDialog;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.NewLine;

public class MapPopupSelectionListener extends SelectionAdapter {

	private static final int DEFAULT_ZOOM_LEVEL = 6;

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private final EarthquakeBulletinGui gui;

	public MapPopupSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		final Earthquake selection = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
		if (selection != null && !tableViewer.getTable().isDisposed()) {
			final MapDialog epicenterMapDialog = new LeafletMapDialog(tableViewer.getTable().getShell());
			epicenterMapDialog.setText(Messages.get("lbl.map.epicenter.title"));
			epicenterMapDialog.setImages(Images.getMainIcons());
			final MapOptions options = epicenterMapDialog.getOptions();
			options.setZoom(DEFAULT_ZOOM_LEVEL);
			final double latitude = selection.getLatitude().doubleValue();
			final double longitude = selection.getLongitude().doubleValue();
			options.setCenterLat(latitude);
			options.setCenterLng(longitude);

			final StringBuilder title = new StringBuilder("M ");
			title.append(selection.getMagnitude()).append(", ").append(selection.getRegion());
			title.append(NewLine.SYSTEM_LINE_SEPARATOR);
			final DateFormat df = ResultsTable.dateFormat.get();
			df.setTimeZone(TimeZone.getTimeZone(configuration.getString(Preference.TIMEZONE, EarthquakeBulletin.Defaults.TIME_ZONE_ID)));
			title.append(df.format(selection.getTime())).append(' ');
			title.append(selection.getLatitude()).append(' ');
			title.append(selection.getLongitude()).append(' ');
			title.append(selection.getDepth()).append(' ');
			title.append(selection.getStatus());

			epicenterMapDialog.getMarkers().add(new MapMarker(latitude, longitude, title.toString()));
			epicenterMapDialog.open();
		}
	}

}
