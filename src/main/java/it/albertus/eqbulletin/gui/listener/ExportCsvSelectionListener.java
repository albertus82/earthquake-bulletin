package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.ResultsTable;

public class ExportCsvSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public ExportCsvSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final ResultsTable resultsTable = gui.getResultsTable();
		if (resultsTable != null) {
			resultsTable.exportCsv();
		}
	}

}
