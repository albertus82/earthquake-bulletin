package com.github.albertus82.eqbulletin.gui.listener;

import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.TypedEvent;

import com.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import com.github.albertus82.eqbulletin.gui.ResultsTable;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileMenuListener implements ArmMenuListener {

	@NonNull
	private final EarthquakeBulletinGui gui;

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
