package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.ResultsTable;
import it.albertus.geofon.client.model.Earthquake;

import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;

public class ResultsTableContextMenuDetectListener implements MenuDetectListener {

	private final ResultsTable resultsTable;

	public ResultsTableContextMenuDetectListener(final ResultsTable resultsTable) {
		this.resultsTable = resultsTable;
	}

	@Override
	public void menuDetected(final MenuDetectEvent mde) {
		final Earthquake selection = (Earthquake) resultsTable.getTableViewer().getStructuredSelection().getFirstElement();
		resultsTable.getShowMapMenuItem().setEnabled(selection != null && selection.getEnclosure() != null);
		resultsTable.getOpenInBrowserMenuItem().setEnabled(selection != null && selection.getLink() != null);
		resultsTable.getGoogleMapsMenuItem().setEnabled(selection != null);
		resultsTable.getContextMenu().setVisible(true);
	}

}
