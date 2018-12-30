package it.albertus.eqbulletin.gui.listener;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.gui.job.MomentTensorRetriever;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;

public class ShowMomentTensorListener implements Listener {

	private final EarthquakeBulletinGui gui;

	public ShowMomentTensorListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake selectedItem = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			final Shell shell = gui.getShell();
			if (selectedItem != null && shell != null && !shell.isDisposed()) {
				final MomentTensor momentTensor = MomentTensorRetriever.retrieve(selectedItem, shell);
				if (momentTensor != null) {
					final MomentTensorDialog dialog = new MomentTensorDialog(shell, momentTensor.getText());
					dialog.open();
				}
			}
		}
	}

}
