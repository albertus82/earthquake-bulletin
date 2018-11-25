package it.albertus.eqbulletin.gui.listener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.jface.maps.MapBounds;
import it.albertus.jface.maps.leaflet.LeafletMapBoundsDialog;

public class MapButtonSelectionListener extends SelectionAdapter {

	private static final DecimalFormat coordinateFormat = new DecimalFormat();

	static {
		final DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator('.');
		coordinateFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		coordinateFormat.setMaximumFractionDigits(2);
		coordinateFormat.setMinimumFractionDigits(2);
		coordinateFormat.setGroupingUsed(false);
	}

	private final SearchForm form;

	public MapButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final LeafletMapBoundsDialog mapBoundsDialog = form.getMapBoundsDialog();
		if (mapBoundsDialog.open() == SWT.OK) {
			final MapBounds bounds = mapBoundsDialog.getBounds();
			if (bounds.getSouthWestLat() != null) {
				form.getLatitudeFromText().setText(coordinateFormat.format(Math.max(SearchForm.LATITUDE_MIN_VALUE, bounds.getSouthWestLat())));
			}
			if (bounds.getNorthEastLat() != null) {
				form.getLatitudeToText().setText(coordinateFormat.format(Math.min(SearchForm.LATITUDE_MAX_VALUE, bounds.getNorthEastLat())));
			}
			if (bounds.getSouthWestLng() != null) {
				form.getLongitudeFromText().setText(coordinateFormat.format(Math.max(SearchForm.LONGITUDE_MIN_VALUE, bounds.getSouthWestLng())));
			}
			if (bounds.getNorthEastLng() != null) {
				form.getLongitudeToText().setText(coordinateFormat.format(Math.min(SearchForm.LONGITUDE_MAX_VALUE, bounds.getNorthEastLng())));
			}
		}
	}

}
