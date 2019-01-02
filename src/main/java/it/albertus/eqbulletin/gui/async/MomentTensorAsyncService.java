package it.albertus.eqbulletin.gui.async;

import java.io.Serializable;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.cache.MomentTensorCache;
import it.albertus.eqbulletin.gui.Images;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.job.MomentTensorDownloadJob;
import it.albertus.eqbulletin.service.net.MomentTensorDownloader;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.SwtUtils;
import it.albertus.util.DaemonThreadFactory;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorAsyncService implements Retriever<Earthquake, MomentTensor> {

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorAsyncService.class);

	private static final ThreadFactory threadFactory = new DaemonThreadFactory();

	public void openDialog(final Earthquake earthquake, final Shell shell) {
		if (earthquake != null && earthquake.getMomentTensorUrl() != null && shell != null && !shell.isDisposed()) {
			try {
				SwtUtils.setWaitCursor(shell);
				final MomentTensor momentTensor = retrieve(earthquake, shell);
				if (momentTensor != null) {
					new MomentTensorDialog(shell, momentTensor, earthquake).open();
				}
			}
			finally {
				SwtUtils.setDefaultCursor(shell);
			}
		}
	}

	@Override
	public MomentTensor retrieve(final Earthquake earthquake, final Shell shell) {
		final MomentTensorCache cache = MomentTensorCache.getInstance();
		final String guid = earthquake.getGuid();
		final MomentTensor cachedObject = cache.get(guid);
		if (cachedObject == null) {
			logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
			try {
				final MomentTensorDownloadJob job = new MomentTensorDownloadJob(earthquake);
				JobRunner.run(job, shell.getDisplay());
				final MomentTensor downloadedObject = job.getDownloadedObject();
				if (downloadedObject != null) {
					cache.put(guid, downloadedObject);
					return downloadedObject; // Avoid unpack from cache the first time.
				}
			}
			catch (final OperationException e) {
				logger.log(e.getLoggingLevel(), e.getMessage());
				if (!shell.isDisposed()) {
					EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), e.getMessage(), e.getSeverity(), e.getCause(), Images.getMainIconArray());
				}
			}
		}
		else {
			logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
			checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake);
		}
		return cache.get(guid);
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MomentTensor cachedObject, final Earthquake earthquake) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final MomentTensor downloadedObject = new MomentTensorDownloader().download(earthquake, cachedObject);
					if (downloadedObject != null && !cachedObject.getText().equals(downloadedObject.getText())) {
						MomentTensorDialog.update(downloadedObject, earthquake); // Update UI on-the-fly.
						MomentTensorCache.getInstance().put(earthquake.getGuid(), downloadedObject);
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			};
			threadFactory.newThread(checkForUpdate).start();
		}
	}

}
