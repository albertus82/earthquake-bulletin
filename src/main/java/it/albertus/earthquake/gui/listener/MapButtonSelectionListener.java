package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.gui.map.MapBoundsDialog;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MapButtonSelectionListener extends SelectionAdapter {

	/** Use {@link #formatCoordinate} method instead. */
	@Deprecated
	private static final DecimalFormat decimalFormat = new DecimalFormat();;

	static {
		final DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator('.');
		decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
		decimalFormat.setMaximumFractionDigits(2);
		decimalFormat.setGroupingUsed(false);
	}

	private static synchronized String formatCoordinate(final double number) {
		return decimalFormat.format(number);
	}

	private final SearchForm form;

	public MapButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final MapBoundsDialog mapBoundsDialog = form.getMapBoundsDialog();
		if (mapBoundsDialog.open() == SWT.OK) {
			if (mapBoundsDialog.getSouthWestLat() != null) {
				form.getLatitudeFromText().setText(formatCoordinate(mapBoundsDialog.getSouthWestLat()));
			}
			if (mapBoundsDialog.getNorthEastLat() != null) {
				form.getLatitudeToText().setText(formatCoordinate(mapBoundsDialog.getNorthEastLat()));
			}
			if (mapBoundsDialog.getSouthWestLng() != null) {
				form.getLongitudeFromText().setText(formatCoordinate(mapBoundsDialog.getSouthWestLng()));
			}
			if (mapBoundsDialog.getNorthEastLng() != null) {
				form.getLongitudeToText().setText(formatCoordinate(mapBoundsDialog.getNorthEastLng()));
			}
		}
	}

}
