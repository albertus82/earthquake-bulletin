package it.albertus.earthquake.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.gui.job.ExportCsvJob;

public class ExportCsvListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public ExportCsvListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final ResultsTable resultsTable = gui.getResultsTable();
		if (resultsTable != null && (resultsTable.getExportCsvJob() == null || resultsTable.getExportCsvJob().getState() == Job.NONE)) {
			resultsTable.setExportCsvJob(new ExportCsvJob(gui));
			resultsTable.getExportCsvJob().schedule();
		}
	}

}

