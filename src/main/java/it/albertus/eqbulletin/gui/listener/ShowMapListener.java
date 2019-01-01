package it.albertus.eqbulletin.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import it.albertus.eqbulletin.cache.MapCache;
import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.gui.async.DownloadMapJob;
import it.albertus.eqbulletin.model.Earthquake;

public class ShowMapListener implements Listener {

	private final EarthquakeBulletinGui gui;

	public ShowMapListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void handleEvent(final Event event) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		if (tableViewer != null && !tableViewer.getTable().isDisposed() && tableViewer.getStructuredSelection() != null) {
			final Earthquake selectedItem = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
			if (selectedItem != null) {
				final String guid = selectedItem.getGuid();
				final MapCanvas mapCanvas = gui.getMapCanvas();
				final MapCache cache = MapCache.getInstance();
				if (mapCanvas.getDownloadMapJob() == null || mapCanvas.getDownloadMapJob().getState() == Job.NONE) {
					if (cache.contains(guid) && cache.get(guid).getEtag() != null) {
						mapCanvas.setDownloadMapJob(new DownloadMapJob(gui, selectedItem, cache.get(guid).getEtag()));
					}
					else {
						mapCanvas.setDownloadMapJob(new DownloadMapJob(gui, selectedItem));
					}
				}
				mapCanvas.getDownloadMapJob().schedule();
			}
		}
	}

}
