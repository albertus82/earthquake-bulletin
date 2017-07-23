package it.albertus.earthquake.gui.job;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.Images;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.MapImage;
import it.albertus.earthquake.resources.Messages;
import it.albertus.earthquake.service.net.ImageDownloader;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

public class DownloadMapJob extends Job {

	private static final Logger logger = LoggerFactory.getLogger(DownloadMapJob.class);

	private final EarthquakeBulletinGui gui;
	private final Earthquake earthquake;
	private final String etag;

	public DownloadMapJob(final EarthquakeBulletinGui gui, final Earthquake earthquake) {
		this(gui, earthquake, null);
	}

	public DownloadMapJob(final EarthquakeBulletinGui gui, final Earthquake earthquake, final String etag) {
		super("Map download");
		this.gui = gui;
		this.earthquake = earthquake;
		this.etag = etag;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Image download", 1);

		if (earthquake.getEnclosure() != null) {
			new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
				@Override
				public void run() {
					gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
				}
			});

			try {
				final MapImage image = ImageDownloader.downloadImage(earthquake.getEnclosure(), etag);

				new DisplayThreadExecutor(gui.getMapCanvas().getCanvas()).execute(new Runnable() {
					@Override
					public void run() {
						if (image != null) {
							gui.getMapCanvas().setImage(earthquake.getGuid(), image);
						}
						else if (gui.getMapCanvas().getCache().contains(earthquake.getGuid())) { // Not modified
							gui.getMapCanvas().setImage(earthquake.getGuid(), gui.getMapCanvas().getCache().get(earthquake.getGuid()));
						}
					}
				});
			}
			catch (final FileNotFoundException e) {
				final String message = Messages.get("err.job.map.not.found");
				logger.log(Level.INFO, message, e);
				new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
					@Override
					public void run() {
						EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.INFO, e, Images.getMainIcons());
					}
				});
			}
			catch (final Exception e) {
				final String message = Messages.get("err.job.map");
				logger.log(Level.WARNING, message, e);
				new DisplayThreadExecutor(gui.getShell()).execute(new Runnable() {
					@Override
					public void run() {
						if (gui.getMapCanvas().getCache().contains(earthquake.getGuid())) { // silently use cached version if available
							gui.getMapCanvas().setImage(earthquake.getGuid(), gui.getMapCanvas().getCache().get(earthquake.getGuid()));
						}
						else {
							EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.WARNING, e, Images.getMainIcons());
						}
					}
				});
			}

			new DisplayThreadExecutor(gui.getMapCanvas().getCanvas()).execute(new Runnable() {
				@Override
				public void run() {
					gui.getShell().setCursor(null);
				}
			});
		}

		monitor.done();
		return Status.OK_STATUS;
	}

}
