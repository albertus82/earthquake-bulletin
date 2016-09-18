package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.model.Earthquake;

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
		resultsTable.getCopyLinkMenuItem().setEnabled(selection != null && selection.getLink() != null);
		resultsTable.getOpenBrowserMenuItem().setEnabled(selection != null && selection.getLink() != null);
		resultsTable.getGoogleMapsBrowserMenuItem().setEnabled(selection != null);
		resultsTable.getGoogleMapsPopupMenuItem().setEnabled(selection != null);
		resultsTable.getContextMenu().setVisible(true);
	}

}
