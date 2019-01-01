package it.albertus.eqbulletin.gui.listener;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.gui.async.MapImageRetriever;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MapImage;

public class ShowMapListener implements Listener {

	private final EarthquakeBulletinGui gui;

	public ShowMapListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake earthquake = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			final Shell shell = gui.getShell();
			if (earthquake != null && earthquake.getEnclosureUrl() != null && shell != null && !shell.isDisposed()) {
				final MapImage mapImage = new MapImageRetriever().retrieve(earthquake, shell);
				if (mapImage != null) {
					MapCanvas.setMapImage(mapImage, earthquake);
				}
			}
		}
	}

}
