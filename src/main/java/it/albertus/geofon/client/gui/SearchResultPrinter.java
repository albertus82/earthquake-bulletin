package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.model.Earthquake;

import java.util.List;

public class SearchResultPrinter implements Runnable {

	private final GeofonClientGui gui;
	private final List<Earthquake> itemsToPrint;

	public SearchResultPrinter(final GeofonClientGui gui, final List<Earthquake> itemsToPrint) {
		this.gui = gui;
		this.itemsToPrint = itemsToPrint;
	}

	@Override
	public void run() {
		// Stampa dei risultati
		final Earthquake[] earthquakes;
		if (itemsToPrint != null && !itemsToPrint.isEmpty()) {
			earthquakes = itemsToPrint.toArray(new Earthquake[0]);
		}
		else {
			earthquakes = new Earthquake[0];
		}
		gui.getResultTable().getTableViewer().setInput(earthquakes);

		// Riabilitazione controlli dopo la ricerca
		// gui.enableControls();

		// Ripristino puntatore del mouse normale
		gui.getShell().setCursor(null);
	}

}
