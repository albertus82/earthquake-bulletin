package io.github.albertus82.eqbulletin.gui.listener;

import java.util.function.Supplier;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.gui.ResultsTable;
import io.github.albertus82.eqbulletin.gui.SearchForm;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.jface.maps.MapBounds;
import io.github.albertus82.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FindSameAreaEventsSelectionListener extends SelectionAdapter {

	private static final double AUTHALIC_RADIUS = 6371.0072;

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final byte SAME_AREA_EVENTS_LATITUDE_INTERVAL = 1;
	}

	private final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

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
			final float offset = configuration.getByte(Preference.SAME_AREA_EVENTS_LATITUDE_INTERVAL, Defaults.SAME_AREA_EVENTS_LATITUDE_INTERVAL);

			// Latitude (parallels)
			final float lat = Math.min(MapBounds.LATITUDE_MAX_VALUE - offset, Math.max(MapBounds.LATITUDE_MIN_VALUE + offset, selection.getLatitude().getValue()));
			form.setLatitudeFrom(lat - offset);
			form.setLatitudeTo(lat + offset);

			// Longitude (meridians)
			final float[] lons = computeLons(lat, selection.getLongitude().getValue(), offset);
			form.setLongitudeFrom(lons[0]);
			form.setLongitudeTo(lons[1]);

			form.getSearchButton().notifyListeners(SWT.Selection, null);
		}
	}

	private static float[] computeLons(final float lat, final float lon, final float offset) {
		final float lat0 = lat - offset;
		final float lat1 = lat + offset;
		float lon0 = lon;
		float lon1 = lon;
		final double targetArea = computeArea(-offset, offset, -offset, offset);
		log.debug("lat={}, lon={}, offset={}, targetArea={}", lat, lon, offset, targetArea);
		double actualArea = 0;
		final float step = 0.01f;
		for (int i = 0; actualArea < targetArea; i++) {
			lon0 -= step;
			lon1 += step;
			actualArea = computeArea(lat0, lat1, lon0, lon1);
			log.debug("lat0={}, lat1={}, lon0={}, lon1={} -> actualArea={}", lat0, lat1, lon0, lon1, actualArea);
			if (i >= 180 / step) { // Full longitude range!
				lon0 = -180;
				lon1 = 180;
				break;
			}
		}
		if (lon0 < -180) {
			lon0 += 360;
		}
		if (lon1 > 180) {
			lon1 -= 360;
		}
		log.debug("lon0={}, lon1={}", lon0, lon1);
		return new float[] { lon0, lon1 };
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
