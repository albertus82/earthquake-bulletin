package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.TypedEvent;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.ResultsTable;

public class FileMenuListener implements ArmMenuListener {

	private final EarthquakeBulletinGui gui;

	public FileMenuListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void menuHidden(final MenuEvent e) {
		gui.getMenuBar().getExportCsvMenuItem().setEnabled(true); // re-enable the accelerator
	}

	@Override
	public void menuArmed(final TypedEvent e) {
		final ResultsTable resultsTable = gui.getResultsTable();
		gui.getMenuBar().getExportCsvMenuItem().setEnabled(resultsTable != null && resultsTable.getTableViewer() != null && resultsTable.getTableViewer().getTable() != null && resultsTable.getTableViewer().getTable().getItemCount() > 0);
	}

}
