package it.albertus.eqbulletin.gui.async;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
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
import it.albertus.util.logging.LoggerFactory;

public class MapImageAsyncOperation extends AsyncOperation<Earthquake> {

	private static final Logger logger = LoggerFactory.getLogger(MapImageAsyncOperation.class);

	private static Job currentJob;

	@Override
	public void execute(final Earthquake earthquake, final Shell shell) {
		if (earthquake != null && earthquake.getEnclosureUrl() != null && shell != null && !shell.isDisposed()) {
			setAppStartingCursor(shell);
			cancelCurrentJob();
			final MapImageCache cache = MapImageCache.getInstance();
			final String guid = earthquake.getGuid();
			final MapImage cachedObject = cache.get(guid);
			if (cachedObject == null) {
				logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
				cacheMiss(earthquake, shell);
			}
			else {
				logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
				cacheHit(cachedObject, earthquake, shell);
			}
		}
	}

	private void cacheHit(final MapImage cachedObject, final Earthquake earthquake, final Shell shell) {
		checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake, shell);
		MapCanvas.setMapImage(cachedObject, earthquake);
	}

	private void cacheMiss(final Earthquake earthquake, final Shell shell) {
		final MapImageDownloadJob job = new MapImageDownloadJob(earthquake);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				try {
					if (!event.getResult().isOK() && event.getResult().getSeverity() != IStatus.CANCEL) {
						throw new AsyncOperationException(job.getResult());
					}
					final MapImage downloadedObject = job.getDownloadedObject();
					if (downloadedObject != null) {
						if (event.getResult().getSeverity() != IStatus.CANCEL) {
							new DisplayThreadExecutor(shell, true).execute(() -> MapCanvas.setMapImage(downloadedObject, earthquake));
						}
						MapImageCache.getInstance().put(earthquake.getGuid(), downloadedObject);
					}
				}
				catch (final AsyncOperationException e) {
					logger.log(e.getLoggingLevel(), e.getMessage());
					if (!shell.isDisposed()) {
						new DisplayThreadExecutor(shell, true).execute(() -> EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), e.getMessage(), e.getSeverity(), e.getCause() != null ? e.getCause() : e, Images.getMainIconArray()));
					}
				}
				finally {
					new DisplayThreadExecutor(shell).execute(() -> setDefaultCursor(shell));
				}
			}
		});
		job.schedule();
		setCurrentJob(job);
	}

	private void checkForUpdateAndRefreshIfNeeded(final MapImage cachedObject, final Earthquake earthquake, final Shell shell) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final MapImage downloadedObject = MapImageDownloader.download(earthquake, cachedObject);
					if (!cachedObject.equals(downloadedObject)) {
						new DisplayThreadExecutor(shell, true).execute(() -> MapCanvas.updateMapImage(downloadedObject, earthquake)); // Update UI on-the-fly.
						MapImageCache.getInstance().put(earthquake.getGuid(), downloadedObject);
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
				finally {
					new DisplayThreadExecutor(shell).execute(() -> setDefaultCursor(shell));
				}
			};
			threadFactory.newThread(checkForUpdate).start();
		}
		else {
			setDefaultCursor(shell);
		}
	}

	private static synchronized void setCurrentJob(final Job job) {
		currentJob = job;
	}

	private static synchronized void cancelCurrentJob() {
		if (currentJob != null) {
			currentJob.cancel();
		}
	}

}
