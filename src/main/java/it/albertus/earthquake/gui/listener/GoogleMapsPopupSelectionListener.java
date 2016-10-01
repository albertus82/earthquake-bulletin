package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.google.maps.MapControl;
import it.albertus.jface.google.maps.MapDialog;
import it.albertus.jface.google.maps.MapMarker;
import it.albertus.jface.google.maps.MapOptions;
import it.albertus.jface.google.maps.MapType;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class GoogleMapsPopupSelectionListener extends SelectionAdapter {

	private final ResultsTable resultsTable;

	public GoogleMapsPopupSelectionListener(final ResultsTable resultsTable) {
		this.resultsTable = resultsTable;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultsTable.getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null && !resultsTable.getTableViewer().getTable().isDisposed()) {
			final MapDialog epicenterMapDialog = new MapDialog(resultsTable.getTableViewer().getTable().getShell());
			epicenterMapDialog.setText(Messages.get("lbl.map.epicenter.title"));
			epicenterMapDialog.setImages(Images.MAIN_ICONS);
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
