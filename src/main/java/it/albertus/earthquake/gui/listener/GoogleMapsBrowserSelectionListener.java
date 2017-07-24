package it.albertus.earthquake.gui.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.model.Earthquake;

public class GoogleMapsBrowserSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public GoogleMapsBrowserSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) gui.getResultsTable().getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null) {
			Program.launch(selection.getGoogleMapsUrl());
		}
	}

}
