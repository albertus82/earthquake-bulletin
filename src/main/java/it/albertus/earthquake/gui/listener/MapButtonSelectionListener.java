package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.gui.map.MapDialog;
import it.albertus.earthquake.resources.Messages;

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
		final MapDialog mapDialog = new MapDialog(form.getFormComposite().getShell());
		mapDialog.setText(Messages.get("lbl.map.title"));
		mapDialog.setImages(Images.MAIN_ICONS);
		if (mapDialog.open() == SWT.OK) {
			if (mapDialog.getSouthWestLat() != null) {
				form.getLatitudeFromText().setText(formatCoordinate(mapDialog.getSouthWestLat()));
			}
			if (mapDialog.getNorthEastLat() != null) {
				form.getLatitudeToText().setText(formatCoordinate(mapDialog.getNorthEastLat()));
			}
			if (mapDialog.getSouthWestLng() != null) {
				form.getLongitudeFromText().setText(formatCoordinate(mapDialog.getSouthWestLng()));
			}
			if (mapDialog.getNorthEastLng() != null) {
				form.getLongitudeToText().setText(formatCoordinate(mapDialog.getNorthEastLng()));
			}
		}
	}

}
