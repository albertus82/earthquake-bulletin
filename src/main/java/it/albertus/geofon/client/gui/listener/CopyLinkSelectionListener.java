package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.ResultsTable;
import it.albertus.geofon.client.model.Earthquake;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

public class CopyLinkSelectionListener extends SelectionAdapter {

	private final ResultsTable resultTable;

	public CopyLinkSelectionListener(final ResultsTable resultTable) {
		this.resultTable = resultTable;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) resultTable.getTableViewer().getStructuredSelection().getFirstElement();
		final Table table = resultTable.getTableViewer().getTable();
		if (selection != null && selection.getLink() != null && table != null && !table.isDisposed()) {
			final Clipboard clipboard = new Clipboard(table.getDisplay());
			clipboard.setContents(new String[] { selection.getLink().toString() }, new Transfer[] { TextTransfer.getInstance() });
			clipboard.dispose();
		}
	}

}
