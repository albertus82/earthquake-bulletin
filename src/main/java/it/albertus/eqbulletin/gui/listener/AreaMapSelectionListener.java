package it.albertus.eqbulletin.gui.listener;

import java.text.NumberFormat;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Text;

import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.jface.maps.CoordinateUtils;
import it.albertus.jface.maps.MapBounds;
import it.albertus.jface.maps.MapBoundsDialog;

public class AreaMapSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public AreaMapSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final MapBoundsDialog mapBoundsDialog = form.getMapBoundsDialog();
		if (mapBoundsDialog.open() == Window.OK) {

			final Text southWestLatText = form.getLatitudeFromText();
			final Text northEastLatText = form.getLatitudeToText();
			final Text southWestLngText = form.getLongitudeFromText();
			final Text northEastLngText = form.getLongitudeToText();

			final MapBounds mb = mapBoundsDialog.getBounds();
			final NumberFormat formatter = CoordinateUtils.getFormatter();
			if (mb.getSouthWestLat() != null && mb.getNorthEastLat() != null) {
				southWestLatText.setText(formatter.format(mb.getSouthWestLat()));
				northEastLatText.setText(formatter.format(mb.getNorthEastLat()));
			}
			if (mb.getSouthWestLng() != null && mb.getNorthEastLng() != null) {
				southWestLngText.setText(formatter.format(mb.getSouthWestLng()));
				northEastLngText.setText(formatter.format(mb.getNorthEastLng()));
			}
		}
	}

}
