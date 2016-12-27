package it.albertus.earthquake.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.MapCache;
import it.albertus.earthquake.gui.job.DownloadMapJob;
import it.albertus.earthquake.model.Earthquake;

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
				final MapCache cache = gui.getMapCanvas().getCache();
				if (cache.contains(guid)) {
					gui.getMapCanvas().setImage(guid, cache.get(guid));
				}
				else {
					if (gui.getMapCanvas().getDownloadMapJob() == null || gui.getMapCanvas().getDownloadMapJob().getState() == Job.NONE) {
						gui.getMapCanvas().setDownloadMapJob(new DownloadMapJob(gui, selectedItem));
						gui.getMapCanvas().getDownloadMapJob().schedule();
					}
				}
			}
		}
	}

}
