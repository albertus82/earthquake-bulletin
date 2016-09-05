package it.albertus.geofon.client.gui.job;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.util.ImageDownloader;
import it.albertus.geofon.client.model.Earthquake;
import it.albertus.jface.SwtThreadExecutor;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.graphics.Image;

public class DownloadMapJob extends Job {

	private final GeofonClientGui gui;
	private final Earthquake earthquake;

	public DownloadMapJob(final GeofonClientGui gui, final Earthquake earthquake) {
		super("Image download");
		this.gui = gui;
		this.earthquake = earthquake;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Image download", 1);

		Image downloadedImage;
		try {
			downloadedImage = ImageDownloader.downloadImage(earthquake.getEnclosure());
		}
		catch (final IOException ioe) {
			ioe.printStackTrace(); // TODO warning: map unavaliable
			downloadedImage = null;
		}
		final Image image = downloadedImage;
		if (image != null) {
			gui.getMapCanvas().getCache().put(earthquake.getGuid(), image);
		}
		new SwtThreadExecutor(gui.getMapCanvas().getCanvas()) {
			@Override
			protected void run() {
				if (image != null) {
					gui.getMapCanvas().setImage(image);
				}
				gui.getShell().setCursor(null);
			}

		}.start();

		monitor.done();
		return Status.OK_STATUS;
	}

}
