package io.github.albertus82.eqbulletin.gui.listener;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import io.github.albertus82.eqbulletin.gui.SearchForm;
import io.github.albertus82.jface.maps.MapBounds;
import io.github.albertus82.jface.maps.MapBoundsDialog;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AreaMapSelectionListener extends SelectionAdapter {

	@NonNull
	private final SearchForm form;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final MapBoundsDialog mapBoundsDialog = form.getMapBoundsDialog();
		if (mapBoundsDialog.open() == Window.OK) {
			final MapBounds bounds = mapBoundsDialog.getBounds();
			if (bounds.getSouthWestLat() != null && bounds.getNorthEastLat() != null) {
				form.setLatitudeFrom(bounds.getSouthWestLat());
				form.setLatitudeTo(bounds.getNorthEastLat());
			}
			if (bounds.getSouthWestLng() != null && bounds.getNorthEastLng() != null) {
				form.setLongitudeFrom(bounds.getSouthWestLng());
				form.setLongitudeTo(bounds.getNorthEastLng());
			}
		}
	}

}
