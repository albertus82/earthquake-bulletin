package io.github.albertus82.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import io.github.albertus82.eqbulletin.config.TimeZoneConfigAccessor;
import io.github.albertus82.eqbulletin.gui.Images;
import io.github.albertus82.eqbulletin.gui.ResultsTable;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.eqbulletin.resources.Leaflet;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.jface.maps.MapMarker;
import io.github.albertus82.jface.maps.leaflet.LeafletMapControl;
import io.github.albertus82.jface.maps.leaflet.LeafletMapDialog;
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
			title.append(selection.getDetails(TimeZoneConfigAccessor.getZoneId()));

			epicenterMapDialog.getMarkers().add(new MapMarker(latitude, longitude, title.toString()));
			epicenterMapDialog.open();
		}
	}

}
