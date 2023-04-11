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

@RequiredArgsConstructor
public class FindEarthquakesSameAreaSelectionListener extends SelectionAdapter {

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
			final float lat = Math.min(MapBounds.LATITUDE_MAX_VALUE - 1f, Math.max(MapBounds.LATITUDE_MIN_VALUE + 1f, selection.getLatitude().getValue()));
			form.setLatitudeFrom(lat - 1);
			form.setLatitudeTo(lat + 1);
			final float lon = Math.min(MapBounds.LONGITUDE_MAX_VALUE - 1f, Math.max(MapBounds.LONGITUDE_MIN_VALUE + 1f, selection.getLongitude().getValue()));
			form.setLongitudeFrom(lon - 1);
			form.setLongitudeTo(lon + 1);
			form.getSearchButton().notifyListeners(SWT.Selection, null);
		}
	}

}
