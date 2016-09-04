package it.albertus.geofon.client.gui.job;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.model.Earthquake;
import it.albertus.jface.SwtThreadExecutor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;

public class DownloadMapJob extends Job {

	private final GeofonClientGui gui;
	private final Earthquake e;

	public DownloadMapJob(final GeofonClientGui gui, final Earthquake e) {
		super("Image download");
		this.gui = gui;
		this.e = e;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Image download", 1);

		final Image image = gui.downloadImage(e.getEnclosure());
		if (image != null) {
			gui.getMapCanvas().getCache().put(e.getGuid(), image);
			new SwtThreadExecutor(gui.getMapCanvas().getCanvas()) {
				@Override
				protected void run() {
					gui.getMapCanvas().setImage(image);
					gui.getShell().setCursor(null);
				}
			}.start();
		}

		monitor.done();
		return Status.OK_STATUS;
	}

}
