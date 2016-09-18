package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.gui.map.MapDialog;
import it.albertus.earthquake.gui.map.Marker;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.resources.Messages;

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
			epicenterMapDialog.setCenterLat(selection.getLatitude().getValue());
			epicenterMapDialog.setCenterLng(selection.getLongitude().getValue());
			epicenterMapDialog.setZoom(6);
			epicenterMapDialog.getMarkers().add(new Marker(selection.getLatitude().getValue(), selection.getLongitude().getValue(), Messages.get("lbl.map.epicenter.marker.title")));
			epicenterMapDialog.open();
		}
	}

}
