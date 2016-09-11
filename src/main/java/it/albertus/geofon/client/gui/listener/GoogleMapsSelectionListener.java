package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.ResultsTable;
import it.albertus.geofon.client.model.Earthquake;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

public class GoogleMapsSelectionListener extends SelectionAdapter {

	private final ResultsTable resultsTable;

	public GoogleMapsSelectionListener(final ResultsTable resultsTable) {
		this.resultsTable = resultsTable;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultsTable.getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null) {
			Program.launch(selection.getGoogleMapsUrl());
		}
	}

}
