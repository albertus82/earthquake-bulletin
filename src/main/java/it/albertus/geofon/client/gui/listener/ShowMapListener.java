package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.MapCache;
import it.albertus.geofon.client.gui.job.DownloadMapJob;
import it.albertus.geofon.client.model.Earthquake;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class ShowMapListener implements Listener {

	private final GeofonClientGui gui;

	public ShowMapListener(final GeofonClientGui gui) {
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
					if ((gui.getMapCanvas().getDownloadMapJob() == null || gui.getMapCanvas().getDownloadMapJob().getState() == Job.NONE)) {
						gui.getMapCanvas().setDownloadMapJob(new DownloadMapJob(gui, selectedItem));
						gui.getMapCanvas().getDownloadMapJob().schedule();
					}
				}
			}
		}
	}

}
