package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.ResultTable;
import it.albertus.geofon.client.model.Earthquake;

import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;

public class ResultTableContextMenuDetectListener implements MenuDetectListener {

	private final ResultTable resultTable;

	public ResultTableContextMenuDetectListener(final ResultTable resultTable) {
		this.resultTable = resultTable;
	}

	@Override
	public void menuDetected(final MenuDetectEvent mde) {
		final Earthquake selection = (Earthquake) resultTable.getTableViewer().getStructuredSelection().getFirstElement();
		resultTable.getOpenInBrowserMenuItem().setEnabled(selection != null && selection.getLink() != null);
		resultTable.getContextMenu().setVisible(true);
	}

}
