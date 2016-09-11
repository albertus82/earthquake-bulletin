package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.ResultsTable;
import it.albertus.geofon.client.model.Earthquake;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

public class GoogleMapsSelectionListener extends SelectionAdapter {

	private final ResultsTable resultTable;

	public GoogleMapsSelectionListener(final ResultsTable resultTable) {
		this.resultTable = resultTable;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultTable.getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null) {
			Program.launch(selection.getGoogleMapsUrl());
		}
	}

}
