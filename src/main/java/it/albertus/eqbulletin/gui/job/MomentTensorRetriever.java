package it.albertus.eqbulletin.gui.job;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.cache.MomentTensorCache;
import it.albertus.eqbulletin.gui.MomentTensorDialog;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.net.MomentTensorDownloader;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.SwtUtils;
import it.albertus.util.DaemonThreadFactory;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorRetriever {

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorRetriever.class);

	private static final ExecutorService executorService = Executors.newSingleThreadExecutor(new DaemonThreadFactory());

	private static MomentTensorFetchJob instance;

	public static MomentTensor retrieve(final Earthquake earthquake, final Shell shell) {
		if (instance != null && instance.getState() != Job.NONE) {
			logger.log(Level.FINE, "Job already running, ignored call for GUID {0}.", earthquake.getGuid());
			return null;
		}
		final MomentTensorCache cache = MomentTensorCache.getInstance();
		final String guid = earthquake.getGuid();
		final MomentTensor cachedMomentTensor = cache.get(guid);
		if (cachedMomentTensor == null) {
			logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}", new Serializable[] { guid, cache.getSize() });
			try {
				SwtUtils.setWaitCursor(shell);
				instance = new MomentTensorFetchJob(earthquake);
				ModalContextRunner.run(instance, shell.getDisplay());
				final MomentTensor momentTensor = instance.getMomentTensor();
				if (momentTensor != null) {
					cache.put(guid, momentTensor);
					return momentTensor; // Avoid unpack from cache the first time.
				}
			}
			catch (final OperationException e) {
				logger.log(e.getLoggingLevel(), e.getMessage());
				SwtUtils.setDefaultCursor(shell);
				if (!shell.isDisposed()) {
					EnhancedErrorDialog.openError(shell, Messages.get("lbl.window.title"), e.getMessage(), e.getSeverity(), e.getCause(), shell.getDisplay().getSystemImage(e.getSystemImageId()));
				}
			}
			finally {
				SwtUtils.setDefaultCursor(shell);
			}
		}
		else {
			logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}", new Serializable[] { guid, cache.getSize() });
			checkForUpdateAndRefreshIfNeeded(cachedMomentTensor, earthquake);
		}
		return cache.get(guid);
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MomentTensor cachedMomentTensor, final Earthquake earthquake) {
		if (cachedMomentTensor.getEtag() != null && !cachedMomentTensor.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final MomentTensor updatedMomentTensor = MomentTensorDownloader.download(earthquake, cachedMomentTensor);
					if (updatedMomentTensor != null && !cachedMomentTensor.getText().equals(updatedMomentTensor.getText())) {
						MomentTensorDialog.update(updatedMomentTensor, earthquake); // Update UI on-the-fly.
						MomentTensorCache.getInstance().put(earthquake.getGuid(), updatedMomentTensor);
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			};
			executorService.execute(checkForUpdate);
		}
	}

	private MomentTensorRetriever() {
		throw new IllegalAccessError();
	}

}
