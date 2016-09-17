package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.model.Earthquake;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

public class OpenInBrowserSelectionListener extends SelectionAdapter {

	private final ResultsTable resultTable;

	public OpenInBrowserSelectionListener(final ResultsTable resultTable) {
		this.resultTable = resultTable;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultTable.getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null && selection.getLink() != null) {
			Program.launch(selection.getLink().toString());
		}
	}

}
