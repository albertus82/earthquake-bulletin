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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AreaMapSelectionListener extends SelectionAdapter {

	@NonNull private final SearchForm form;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final MapBoundsDialog mapBoundsDialog = form.getMapBoundsDialog();
		if (mapBoundsDialog.open() == Window.OK) {

			final Text southWestLatText = form.getLatitudeFromText();
			final Text northEastLatText = form.getLatitudeToText();
			final Text southWestLngText = form.getLongitudeFromText();
			final Text northEastLngText = form.getLongitudeToText();

			final MapBounds bounds = mapBoundsDialog.getBounds();
			final NumberFormat numberFormat = CoordinateUtils.newFormatter();
			if (bounds.getSouthWestLat() != null && bounds.getNorthEastLat() != null) {
				southWestLatText.setText(numberFormat.format(bounds.getSouthWestLat()));
				northEastLatText.setText(numberFormat.format(bounds.getNorthEastLat()));
			}
			if (bounds.getSouthWestLng() != null && bounds.getNorthEastLng() != null) {
				southWestLngText.setText(numberFormat.format(bounds.getSouthWestLng()));
				northEastLngText.setText(numberFormat.format(bounds.getNorthEastLng()));
			}
		}
	}

}
