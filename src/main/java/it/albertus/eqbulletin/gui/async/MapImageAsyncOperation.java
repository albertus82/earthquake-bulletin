package it.albertus.eqbulletin.gui.async;

import static it.albertus.jface.DisplayThreadExecutor.Mode.ASYNC;
import static it.albertus.jface.DisplayThreadExecutor.Mode.SYNC;

import java.util.Optional;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.cache.MapImageCache;
import it.albertus.eqbulletin.gui.MapCanvas;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.eqbulletin.service.job.MapImageDownloadJob;
import it.albertus.eqbulletin.service.net.MapImageDownloader;
import it.albertus.jface.DisplayThreadExecutor;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapImageAsyncOperation extends AsyncOperation {

	@Setter
	private static Job currentJob;

	public static synchronized void execute(final Earthquake earthquake, final Shell shell) {
		if (earthquake != null && earthquake.getEnclosureUri().isPresent() && shell != null && !shell.isDisposed()) {
			setAppStartingCursor(shell);
			cancelCurrentJob();
			final MapImageCache cache = MapImageCache.getInstance();
			final String guid = earthquake.getGuid();
			final MapImage cachedObject = cache.get(guid);
			if (cachedObject == null) {
				log.debug("Cache miss for key \"{}\". Cache size: {}.", guid, cache.getSize());
				cacheMiss(earthquake, shell);
			}
			else {
				log.debug("Cache hit for key \"{}\". Cache size: {}.", guid, cache.getSize());
				cacheHit(cachedObject, earthquake, shell);
			}
		}
	}

	private static void cacheHit(final MapImage cachedObject, final Earthquake earthquake, final Shell shell) {
		checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake, shell);
		MapCanvas.setMapImage(cachedObject, earthquake);
	}

	private static void cacheMiss(final Earthquake earthquake, final Shell shell) {
		final MapImageDownloadJob job = new MapImageDownloadJob(earthquake);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				try {
					if (!event.getResult().isOK() && event.getResult().getSeverity() != IStatus.CANCEL) {
						throw new AsyncOperationException(job.getResult());
					}
					final Optional<MapImage> downloadedObject = job.getDownloadedObject();
					if (downloadedObject.isPresent()) {
						if (event.getResult().getSeverity() != IStatus.CANCEL) {
							new DisplayThreadExecutor(shell, ASYNC).execute(() -> MapCanvas.setMapImage(downloadedObject.get(), earthquake));
						}
						MapImageCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
					}
				}
				catch (final AsyncOperationException e) {
					e.log(log);
					showErrorDialog(e, shell);
				}
				finally {
					new DisplayThreadExecutor(shell, SYNC).execute(() -> setDefaultCursor(shell));
				}
			}
		});
		job.schedule();
		setCurrentJob(job);
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MapImage cachedObject, final Earthquake earthquake, final Shell shell) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final Optional<MapImage> downloadedObject = new MapImageDownloader().download(earthquake, cachedObject, () -> false);
					if (downloadedObject.isPresent() && !downloadedObject.get().equals(cachedObject)) {
						new DisplayThreadExecutor(shell, ASYNC).execute(() -> MapCanvas.updateMapImage(downloadedObject.get(), earthquake)); // Update UI on-the-fly.
						MapImageCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
					}
				}
				catch (final Exception e) {
					log.warn("An error occurred while checking for map image updates:", e);
				}
				finally {
					new DisplayThreadExecutor(shell, SYNC).execute(() -> setDefaultCursor(shell));
				}
			};
			threadFactory.newThread(checkForUpdate).start();
		}
		else {
			setDefaultCursor(shell);
		}
	}

	private static void cancelCurrentJob() {
		if (currentJob != null) {
			currentJob.cancel();
			currentJob = null;
		}
	}

}
