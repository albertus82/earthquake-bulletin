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
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.google.maps.MapControl;
import it.albertus.jface.google.maps.MapDialog;
import it.albertus.jface.google.maps.MapMarker;
import it.albertus.jface.google.maps.MapOptions;
import it.albertus.jface.google.maps.MapType;
import it.albertus.util.Configuration;
import it.albertus.util.NewLine;

public class GoogleMapsPopupSelectionListener extends SelectionAdapter {

	private static final Configuration configuration = EarthquakeBulletinConfig.getInstance();

	private final EarthquakeBulletinGui gui;

	public GoogleMapsPopupSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		final Earthquake selection = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
		if (selection != null && !tableViewer.getTable().isDisposed()) {
			final MapDialog epicenterMapDialog = new MapDialog(tableViewer.getTable().getShell());
			epicenterMapDialog.setText(Messages.get("lbl.map.epicenter.title"));
			epicenterMapDialog.setImages(Images.getMainIcons());
			final MapOptions options = epicenterMapDialog.getOptions();
			options.setZoom(6);
			options.setType(MapType.TERRAIN);
			options.getControls().put(MapControl.SCALE, true);
			final double latitude = selection.getLatitude().doubleValue();
			final double longitude = selection.getLongitude().doubleValue();
			options.setCenterLat(latitude);
			options.setCenterLng(longitude);

			final StringBuilder title = new StringBuilder("M ");
			title.append(selection.getMagnitude()).append(", ").append(selection.getRegion());
			title.append(NewLine.SYSTEM_LINE_SEPARATOR);
			final DateFormat df = ResultsTable.dateFormat.get();
			df.setTimeZone(TimeZone.getTimeZone(configuration.getString("timezone", EarthquakeBulletin.Defaults.TIME_ZONE_ID)));
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
