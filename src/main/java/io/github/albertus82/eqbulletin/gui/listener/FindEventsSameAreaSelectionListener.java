package io.github.albertus82.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

import io.github.albertus82.eqbulletin.gui.ResultsTable;
import io.github.albertus82.eqbulletin.gui.SearchForm;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.jface.maps.MapBounds;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FindEventsSameAreaSelectionListener extends SelectionAdapter {

	private static final double AUTHALIC_RADIUS = 6371.0072;

	@NonNull
	private final Supplier<ResultsTable> resultsTableSupplier;

	@NonNull
	private final Supplier<SearchForm> searchFormSupplier;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final TableViewer tableViewer = resultsTableSupplier.get().getTableViewer();
		final Earthquake selection = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
		final Table table = tableViewer.getTable();
		if (selection != null && table != null && !table.isDisposed()) {
			final SearchForm form = searchFormSupplier.get();
			final float offset = 1;

			// Latitude (parallels)
			final float lat = Math.min(MapBounds.LATITUDE_MAX_VALUE - offset, Math.max(MapBounds.LATITUDE_MIN_VALUE + offset, selection.getLatitude().getValue()));
			final float lat0 = lat - offset;
			final float lat1 = lat + offset;
			form.setLatitudeFrom(lat0);
			form.setLatitudeTo(lat1);

			// Longitude (meridians)
			final float lon = selection.getLongitude().getValue();
			float lon0 = lon;
			float lon1 = lon;
			final double targetArea = computeArea(-1, 1, -1, 1);
			double actualArea;
			do {
				final float step = 0.01f;
				lon0 -= step;
				lon1 += step;
				actualArea = computeArea(lat0, lat1, lon0, lon1);
				log.debug("lon0={}, lon1={} -> actualArea={}", lon0, lon1, actualArea);
			}
			while (actualArea < targetArea);
			if (lon0 < -180) {
				lon0 += 360;
			}
			if (lon1 > 180) {
				lon1 -= 360;
			}
			log.debug("lon0={}, lon1={}", lon0, lon1);
			form.setLongitudeFrom(lon0);
			form.setLongitudeTo(lon1);

			form.getSearchButton().notifyListeners(SWT.Selection, null);
		}
	}

	private static double computeArea(final double lat0deg, final double lat1deg, final double lon0deg, final double lon1deg) {
		final double lat0rad = Math.toRadians(lat0deg);
		final double lat1rad = Math.toRadians(lat1deg);
		final double lon0rad = Math.toRadians(lon0deg);
		final double lon1rad = Math.toRadians(lon1deg);
		final double a = Math.sin(lat1rad) - Math.sin(lat0rad);
		final double b = lon1rad - lon0rad;
		final double c = AUTHALIC_RADIUS * AUTHALIC_RADIUS;
		return a * b * c;
	}

}
