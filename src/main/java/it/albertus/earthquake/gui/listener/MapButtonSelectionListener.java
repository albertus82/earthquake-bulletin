package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.map.MapDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MapButtonSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public MapButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final MapDialog mapDialog = new MapDialog(form.getFormComposite().getShell());
		if (mapDialog.open() == SWT.OK) {
			if (mapDialog.getSouthWestLat() != null) {
				form.getLatitudeFromText().setText(mapDialog.getSouthWestLat().toString());
			}
			if (mapDialog.getNorthEastLat() != null) {
				form.getLatitudeToText().setText(mapDialog.getNorthEastLat().toString());
			}
			if (mapDialog.getSouthWestLng() != null) {
				form.getLongitudeFromText().setText(mapDialog.getSouthWestLng().toString());
			}
			if (mapDialog.getNorthEastLng() != null) {
				form.getLongitudeToText().setText(mapDialog.getNorthEastLng().toString());
			}
		}
	}

}
