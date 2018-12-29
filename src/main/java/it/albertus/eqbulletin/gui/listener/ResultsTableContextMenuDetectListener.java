package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.widgets.Table;

import it.albertus.eqbulletin.gui.ResultsTable;
import it.albertus.eqbulletin.gui.ResultsTable.ContextMenu;
import it.albertus.eqbulletin.model.Earthquake;

public class ResultsTableContextMenuDetectListener implements MenuDetectListener {

	private final ResultsTable resultsTable;

	public ResultsTableContextMenuDetectListener(final ResultsTable resultsTable) {
		this.resultsTable = resultsTable;
	}

	@Override
	public void menuDetected(final MenuDetectEvent mde) {
		final Earthquake selection = (Earthquake) resultsTable.getTableViewer().getStructuredSelection().getFirstElement();
		final ContextMenu contextMenu = resultsTable.getContextMenu();
		contextMenu.getShowMapMenuItem().setEnabled(selection != null && selection.getEnclosure() != null);
		contextMenu.getShowMomentTensorMenuItem().setEnabled(selection != null && selection.getMomentTensor() != null);
		contextMenu.getCopyLinkMenuItem().setEnabled(selection != null && selection.getLink() != null);
		contextMenu.getOpenBrowserMenuItem().setEnabled(selection != null && selection.getLink() != null);
		contextMenu.getGoogleMapsBrowserMenuItem().setEnabled(selection != null);
		contextMenu.getEpicenterMapPopupMenuItem().setEnabled(selection != null);
		final Table table = resultsTable.getTableViewer().getTable();
		contextMenu.getExportCsvMenuItem().setEnabled(table != null && table.getItemCount() > 0);
		contextMenu.getMenu().setVisible(true);
	}

}
