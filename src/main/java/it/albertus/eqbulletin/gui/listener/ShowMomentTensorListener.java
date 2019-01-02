package it.albertus.eqbulletin.gui.listener;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.async.MomentTensorAsyncOperation;
import it.albertus.eqbulletin.model.Earthquake;

public class ShowMomentTensorListener implements Listener {

	private final EarthquakeBulletinGui gui;

	public ShowMomentTensorListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake earthquake = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			new MomentTensorAsyncOperation().execute(earthquake, gui.getShell());
		}
	}

}
