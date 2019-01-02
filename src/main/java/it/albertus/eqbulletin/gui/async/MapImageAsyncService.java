package it.albertus.eqbulletin.gui.async;

import java.io.Serializable;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.cache.MapImageCache;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.job.MapImageDownloadJob;
import it.albertus.eqbulletin.service.net.MapImageDownloader;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.DaemonThreadFactory;
import it.albertus.util.logging.LoggerFactory;

public class MapImageAsyncService {

	private static final Logger logger = LoggerFactory.getLogger(MapImageAsyncService.class);

	private static final ThreadFactory threadFactory = new DaemonThreadFactory();

	public static void setCanvasImage(final Earthquake earthquake, final Shell shell) {
		if (earthquake != null && earthquake.getEnclosureUrl() != null && shell != null && !shell.isDisposed()) {
			JobManager.setWaitCursor(shell);
			execute(earthquake, shell);
		}
	}

	private static void execute(final Earthquake earthquake, final Shell shell) {
		final MapImageCache cache = MapImageCache.getInstance();
		final String guid = earthquake.getGuid();
		final MapImage cachedObject = cache.get(guid);
		if (cachedObject == null) {
			logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
			final MapImageDownloadJob job = new MapImageDownloadJob(earthquake);
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(final IJobChangeEvent event) {
					try {
						if (!event.getResult().isOK()) {
							throw new OperationException(job.getResult());
						}
						final MapImage downloadedObject = job.getDownloadedObject();
						if (downloadedObject != null) {
							cache.put(guid, downloadedObject);
							new DisplayThreadExecutor(shell).execute(() -> MapCanvas.setMapImage(downloadedObject, earthquake));
						}
					}
					catch (final OperationException e) {
						logger.log(e.getLoggingLevel(), e.getMessage());
						if (!shell.isDisposed()) {
							new DisplayThreadExecutor(shell).execute(() -> EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), e.getMessage(), e.getSeverity(), e.getCause(), Images.getMainIconArray()));
						}
					}
					finally {
						new DisplayThreadExecutor(shell).execute(() -> JobManager.setDefaultCursor(shell));
					}
				}
			});
			job.schedule();
		}
		else {
			logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
			MapCanvas.setMapImage(cachedObject, earthquake);
			checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake, shell);
		}
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MapImage cachedObject, final Earthquake earthquake, final Shell shell) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final MapImage downloadedObject = MapImageDownloader.download(earthquake, cachedObject);
					if (!cachedObject.equals(downloadedObject)) {
						new DisplayThreadExecutor(shell).execute(() -> MapCanvas.updateMapImage(downloadedObject, earthquake)); // Update UI on-the-fly.
						MapImageCache.getInstance().put(earthquake.getGuid(), downloadedObject);
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
				finally {
					new DisplayThreadExecutor(shell).execute(() -> JobManager.setDefaultCursor(shell));
				}
			};
			threadFactory.newThread(checkForUpdate).start();
		}
	}

	private MapImageAsyncService() {
		throw new IllegalAccessError();
	}

}
