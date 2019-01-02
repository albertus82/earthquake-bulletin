package it.albertus.eqbulletin.gui.async;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.cache.MomentTensorCache;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.job.MomentTensorDownloadJob;
import it.albertus.eqbulletin.service.net.MomentTensorDownloader;
import it.albertus.jface.DisplayThreadExecutor;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorAsyncOperation extends AsyncOperation<Earthquake> {

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorAsyncOperation.class);

	@Override
	public void execute(final Earthquake earthquake, final Shell shell) {
		if (earthquake != null && earthquake.getMomentTensorUrl() != null && shell != null && !shell.isDisposed()) {
			setWaitCursor(shell);
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

	private void cacheHit(final MomentTensor cachedObject, final Earthquake earthquake, final Shell shell) {
		checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake, shell);
		new MomentTensorDialog(shell, cachedObject, earthquake).open(); // Blocking!
	}

	private void cacheMiss(final Earthquake earthquake, final Shell shell) {
		final MomentTensorDownloadJob job = new MomentTensorDownloadJob(earthquake);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				try {
					if (!event.getResult().isOK()) {
						throw new AsyncOperationException(job.getResult());
					}
					final MomentTensor downloadedObject = job.getDownloadedObject();
					if (downloadedObject != null) {
						MomentTensorCache.getInstance().put(earthquake.getGuid(), downloadedObject);
						new DisplayThreadExecutor(shell).execute(() -> new MomentTensorDialog(shell, downloadedObject, earthquake).open());
					}
				}
				catch (final AsyncOperationException e) {
					logger.log(e.getLoggingLevel(), e.getMessage());
					if (!shell.isDisposed()) {
						new DisplayThreadExecutor(shell).execute(() -> EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), e.getMessage(), e.getSeverity(), e.getCause(), Images.getMainIconArray()));
					}
				}
				finally {
					new DisplayThreadExecutor(shell).execute(() -> setDefaultCursor(shell));
				}
			}
		});
		job.schedule();
	}

	private void checkForUpdateAndRefreshIfNeeded(final MomentTensor cachedObject, final Earthquake earthquake, final Shell shell) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final MomentTensor downloadedObject = MomentTensorDownloader.download(earthquake, cachedObject);
					if (downloadedObject != null && !cachedObject.getText().equals(downloadedObject.getText())) {
						new DisplayThreadExecutor(shell).execute(() -> MomentTensorDialog.updateMomentTensorText(downloadedObject, earthquake)); // Update UI on-the-fly.
						MomentTensorCache.getInstance().put(earthquake.getGuid(), downloadedObject);
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
	}

}
