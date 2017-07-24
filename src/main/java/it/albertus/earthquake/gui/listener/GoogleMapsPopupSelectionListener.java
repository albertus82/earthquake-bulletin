package it.albertus.earthquake.gui.listener;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.google.maps.MapControl;
import it.albertus.jface.google.maps.MapDialog;
import it.albertus.jface.google.maps.MapMarker;
import it.albertus.jface.google.maps.MapOptions;
import it.albertus.jface.google.maps.MapType;

public class GoogleMapsPopupSelectionListener extends SelectionAdapter {

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
			epicenterMapDialog.getMarkers().add(new MapMarker(latitude, longitude, Messages.get("lbl.map.epicenter.marker.title")));
			epicenterMapDialog.open();
		}
	}

}
