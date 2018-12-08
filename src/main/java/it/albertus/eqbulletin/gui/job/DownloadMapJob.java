package it.albertus.eqbulletin.gui.job;

import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.net.ImageDownloader;
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
		monitor.beginTask("Map download", IProgressMonitor.UNKNOWN);

		if (earthquake.getEnclosure() != null) {
			final MapCanvas mapCanvas = gui.getMapCanvas();

			new DisplayThreadExecutor(gui.getShell()).execute(() -> {
				gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
				if (mapCanvas.getCache().contains(earthquake.getGuid())) { // show cached map immediately if available
					mapCanvas.setImage(earthquake.getGuid(), mapCanvas.getCache().get(earthquake.getGuid()));
				}
			});

			try {
				final MapImage image = ImageDownloader.downloadImage(earthquake.getEnclosure(), etag);

				if (image != null) {
					new DisplayThreadExecutor(mapCanvas.getCanvas()).execute(() -> mapCanvas.setImage(earthquake.getGuid(), image));
				}
			}
			catch (final FileNotFoundException e) {
				final String message = Messages.get("err.job.map.not.found");
				logger.log(Level.INFO, message, e);
				new DisplayThreadExecutor(gui.getShell()).execute(() -> EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.INFO, e, Images.getMainIcons())); // always show error dialog in this case
			}
			catch (final Exception | LinkageError e) {
				final String message = Messages.get("err.job.map");
				logger.log(Level.WARNING, message, e);
				if (!mapCanvas.getCache().contains(earthquake.getGuid())) { // show error dialog only if not present in cache
					new DisplayThreadExecutor(gui.getShell()).execute(() -> EnhancedErrorDialog.openError(gui.getShell(), Messages.get("lbl.window.title"), message, IStatus.WARNING, e, Images.getMainIcons()));
				}
			}

			new DisplayThreadExecutor(mapCanvas.getCanvas()).execute(() -> gui.getShell().setCursor(null));
		}

		monitor.done();
		return Status.OK_STATUS;
	}

}
