package it.albertus.earthquake.gui.listener;

import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;

import it.albertus.earthquake.gui.ResultsTable;
import it.albertus.earthquake.model.Earthquake;

public class ResultsTableContextMenuDetectListener implements MenuDetectListener {

	private final ResultsTable resultsTable;

	public ResultsTableContextMenuDetectListener(final ResultsTable resultsTable) {
		this.resultsTable = resultsTable;
	}

	@Override
	public void menuDetected(final MenuDetectEvent mde) {
		final Earthquake selection = (Earthquake) resultsTable.getTableViewer().getStructuredSelection().getFirstElement();
		resultsTable.getContextMenu().getShowMapMenuItem().setEnabled(selection != null && selection.getEnclosure() != null);
		resultsTable.getContextMenu().getCopyLinkMenuItem().setEnabled(selection != null && selection.getLink() != null);
		resultsTable.getContextMenu().getOpenBrowserMenuItem().setEnabled(selection != null && selection.getLink() != null);
		resultsTable.getContextMenu().getGoogleMapsBrowserMenuItem().setEnabled(selection != null);
		resultsTable.getContextMenu().getGoogleMapsPopupMenuItem().setEnabled(selection != null);
		resultsTable.getContextMenu().getExportCsvMenuItem().setEnabled(resultsTable.getTableViewer().getTable() != null && resultsTable.getTableViewer().getTable().getItemCount() > 0);
		resultsTable.getContextMenu().getMenu().setVisible(true);
	}

}
