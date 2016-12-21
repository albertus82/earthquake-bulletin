package it.albertus.earthquake.gui.listener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.jface.google.maps.MapBounds;
import it.albertus.jface.google.maps.MapBoundsDialog;

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
		final MapBoundsDialog mapBoundsDialog = form.getMapBoundsDialog();
		if (mapBoundsDialog.open() == SWT.OK) {
			final MapBounds bounds = mapBoundsDialog.getBounds();
			if (bounds.getSouthWestLat() != null) {
				form.getLatitudeFromText().setText(coordinateFormat.format(bounds.getSouthWestLat()));
			}
			if (bounds.getNorthEastLat() != null) {
				form.getLatitudeToText().setText(coordinateFormat.format(bounds.getNorthEastLat()));
			}
			if (bounds.getSouthWestLng() != null) {
				form.getLongitudeFromText().setText(coordinateFormat.format(bounds.getSouthWestLng()));
			}
			if (bounds.getNorthEastLng() != null) {
				form.getLongitudeToText().setText(coordinateFormat.format(bounds.getNorthEastLng()));
			}
		}
	}

}
