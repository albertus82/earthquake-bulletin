package it.albertus.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.eqbulletin.config.TimeZoneConfig;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.resources.Leaflet;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.maps.MapMarker;
import it.albertus.jface.maps.leaflet.LeafletMapControl;
import it.albertus.jface.maps.leaflet.LeafletMapDialog;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EpicenterMapSelectionListener extends SelectionAdapter {

	private static final int DEFAULT_ZOOM_LEVEL = 6;

	@NonNull
	private final Supplier<ResultsTable> resultsTableSupplier;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final TableViewer tableViewer = resultsTableSupplier.get().getTableViewer();
		final Earthquake selection = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
		if (selection != null && !tableViewer.getTable().isDisposed()) {
			final LeafletMapDialog epicenterMapDialog = new LeafletMapDialog(tableViewer.getTable().getShell());
			epicenterMapDialog.setText(Messages.get("label.map.epicenter.title"));
			epicenterMapDialog.setImages(Images.getAppIconArray());
			epicenterMapDialog.getOptions().setZoom(DEFAULT_ZOOM_LEVEL);
			epicenterMapDialog.getOptions().getControls().put(LeafletMapControl.ZOOM, "");
			epicenterMapDialog.getOptions().getControls().put(LeafletMapControl.ATTRIBUTION, "");
			epicenterMapDialog.getOptions().getControls().put(LeafletMapControl.SCALE, "");
			if (Leaflet.LAYERS != null && !Leaflet.LAYERS.isEmpty()) {
				epicenterMapDialog.getOptions().getControls().put(LeafletMapControl.LAYERS, Leaflet.LAYERS);
			}
			final double latitude = selection.getLatitude().doubleValue();
			final double longitude = selection.getLongitude().doubleValue();
			epicenterMapDialog.getOptions().setCenterLat(latitude);
			epicenterMapDialog.getOptions().setCenterLng(longitude);

			final StringBuilder title = new StringBuilder();
			title.append(selection.getSummary());
			title.append(System.lineSeparator());
			title.append(selection.getDetails(TimeZoneConfig.getZoneId()));

			epicenterMapDialog.getMarkers().add(new MapMarker(latitude, longitude, title.toString()));
			epicenterMapDialog.open();
		}
	}

}
