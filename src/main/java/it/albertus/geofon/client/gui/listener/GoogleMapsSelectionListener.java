package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.ResultTable;
import it.albertus.geofon.client.model.Earthquake;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

public class GoogleMapsSelectionListener extends SelectionAdapter {

	private final ResultTable resultTable;

	public GoogleMapsSelectionListener(final ResultTable resultTable) {
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
