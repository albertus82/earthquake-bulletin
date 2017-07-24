package it.albertus.earthquake.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.ResultsTable;

public class FileMenuListener implements ArmListener, MenuListener {

	private final EarthquakeBulletinGui gui;

	public FileMenuListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetArmed(final ArmEvent e) {
		manageExportCsvItem();
	}

	@Override
	public void menuShown(final MenuEvent e) {
		manageExportCsvItem();
	}

	@Override
	public void menuHidden(final MenuEvent e) {
		gui.getMenuBar().getFileExportCsvItem().setEnabled(true); // re-enable the accelerator
	}

	private void manageExportCsvItem() {
		final ResultsTable resultsTable = gui.getResultsTable();
		gui.getMenuBar().getFileExportCsvItem().setEnabled(resultsTable != null && resultsTable.getTableViewer() != null && resultsTable.getTableViewer().getTable() != null && resultsTable.getTableViewer().getTable().getItemCount() > 0 && (resultsTable.getExportCsvJob() == null || resultsTable.getExportCsvJob().getState() == Job.NONE));
	}

}
