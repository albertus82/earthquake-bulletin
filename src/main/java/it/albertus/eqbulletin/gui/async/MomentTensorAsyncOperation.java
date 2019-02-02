package it.albertus.eqbulletin.gui.async;

import static it.albertus.jface.DisplayThreadExecutor.Mode.ASYNC;
import static it.albertus.jface.DisplayThreadExecutor.Mode.SYNC;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.cache.MomentTensorCache;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.service.job.MomentTensorDownloadJob;
import it.albertus.eqbulletin.service.net.MomentTensorDownloader;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorAsyncOperation extends AsyncOperation {

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorAsyncOperation.class);

	public static void execute(final Earthquake earthquake, final Shell shell) {
		if (earthquake != null && earthquake.getMomentTensorUrl() != null && shell != null && !shell.isDisposed()) {
			setAppStartingCursor(shell);
			final MomentTensorCache cache = MomentTensorCache.getInstance();
			final String guid = earthquake.getGuid();
			final MomentTensor cachedObject = cache.get(guid);
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

	private static void cacheHit(final MomentTensor cachedObject, final Earthquake earthquake, final Shell shell) {
		final MomentTensorDialog dialog = new MomentTensorDialog(shell, cachedObject, earthquake);
		checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake, shell); // Async
		dialog.open(); // Blocking!
	}

	private static void cacheMiss(final Earthquake earthquake, final Shell shell) {
		final MomentTensorDownloadJob job = new MomentTensorDownloadJob(earthquake);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				try {
					if (!event.getResult().isOK()) {
						throw new AsyncOperationException(job.getResult());
					}
					final Optional<MomentTensor> downloadedObject = job.getDownloadedObject();
					if (downloadedObject.isPresent()) {
						new DisplayThreadExecutor(shell, ASYNC).execute(() -> new MomentTensorDialog(shell, downloadedObject.get(), earthquake).open());
						MomentTensorCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
					}
				}
				catch (final AsyncOperationException e) {
					showErrorDialog(e, shell);
				}
				finally {
					new DisplayThreadExecutor(shell, SYNC).execute(() -> setDefaultCursor(shell));
				}
			}
		});
		job.schedule();
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MomentTensor cachedObject, final Earthquake earthquake, final Shell shell) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final Optional<MomentTensor> downloadedObject = MomentTensorDownloader.download(earthquake, cachedObject);
					if (downloadedObject.isPresent() && !downloadedObject.get().equals(cachedObject)) {
						new DisplayThreadExecutor(shell, ASYNC).execute(() -> MomentTensorDialog.updateMomentTensorText(downloadedObject.get(), earthquake)); // Update UI on-the-fly.
						MomentTensorCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
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

}
