package it.albertus.earthquake.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.MapCanvas;
import it.albertus.earthquake.gui.job.DownloadMapJob;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.service.MapCache;

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
				final MapCache cache = mapCanvas.getCache();
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
