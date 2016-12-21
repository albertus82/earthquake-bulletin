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

	private static final ThreadLocal<DecimalFormat> coordinateFormat = new ThreadLocal<DecimalFormat>() {
		@Override
		protected DecimalFormat initialValue() {
			final DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
			decimalFormatSymbols.setDecimalSeparator('.');
			final DecimalFormat decimalFormat = new DecimalFormat();
			decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
			decimalFormat.setMaximumFractionDigits(2);
			decimalFormat.setMinimumFractionDigits(2);
			decimalFormat.setGroupingUsed(false);
			return decimalFormat;
		}
	};

	private final SearchForm form;

	public MapButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final MapBoundsDialog mapBoundsDialog = form.getMapBoundsDialog();
		if (mapBoundsDialog.open() == SWT.OK) {
			final MapBounds bounds = mapBoundsDialog.getBounds();
			final DecimalFormat cf = coordinateFormat.get();
			if (bounds.getSouthWestLat() != null) {
				form.getLatitudeFromText().setText(cf.format(bounds.getSouthWestLat()));
			}
			if (bounds.getNorthEastLat() != null) {
				form.getLatitudeToText().setText(cf.format(bounds.getNorthEastLat()));
			}
			if (bounds.getSouthWestLng() != null) {
				form.getLongitudeFromText().setText(cf.format(bounds.getSouthWestLng()));
			}
			if (bounds.getNorthEastLng() != null) {
				form.getLongitudeToText().setText(cf.format(bounds.getNorthEastLng()));
			}
		}
	}

}
