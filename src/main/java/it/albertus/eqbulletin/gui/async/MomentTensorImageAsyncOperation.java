package it.albertus.eqbulletin.gui.async;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import it.albertus.eqbulletin.cache.MomentTensorImageCache;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensorImage;
import it.albertus.eqbulletin.service.job.MomentTensorImageDownloadJob;
import it.albertus.eqbulletin.service.net.MomentTensorImageDownloader;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorImageAsyncOperation extends AsyncOperation {

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorImageAsyncOperation.class);

	private static Job currentJob;

	public static synchronized void execute(final Earthquake earthquake) {
		if (earthquake != null && earthquake.getMomentTensorUri().isPresent()) {
			cancelCurrentJob();
			final MomentTensorImageCache cache = MomentTensorImageCache.getInstance();
			final String guid = earthquake.getGuid();
			final MomentTensorImage cachedObject = cache.get(guid);
			if (cachedObject == null) {
				logger.log(Level.FINE, "Cache miss for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
				cacheMiss(earthquake);
			}
			else {
				logger.log(Level.FINE, "Cache hit for key \"{0}\". Cache size: {1}.", new Serializable[] { guid, cache.getSize() });
				cacheHit(cachedObject, earthquake);
			}
		}
	}

	private static void cacheHit(final MomentTensorImage cachedObject, final Earthquake earthquake) {
		checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake);
	}

	private static void cacheMiss(final Earthquake earthquake) {
		final MomentTensorImageDownloadJob job = new MomentTensorImageDownloadJob(earthquake);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				try {
					if (!event.getResult().isOK() && event.getResult().getSeverity() != IStatus.CANCEL) {
						throw new AsyncOperationException(job.getResult());
					}
					final Optional<MomentTensorImage> downloadedObject = job.getDownloadedObject();
					if (downloadedObject.isPresent()) {
						MomentTensorImageCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
					}
				}
				catch (final AsyncOperationException e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			}
		});
		job.schedule();
		setCurrentJob(job);
	}

	private static void checkForUpdateAndRefreshIfNeeded(final MomentTensorImage cachedObject, final Earthquake earthquake) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final Optional<MomentTensorImage> downloadedObject = new MomentTensorImageDownloader().download(earthquake, cachedObject, () -> false);
					if (downloadedObject.isPresent() && !downloadedObject.get().equals(cachedObject)) {
						MomentTensorImageCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
					}
				}
				catch (final Exception e) {
					logger.log(Level.WARNING, e.toString(), e);
				}
			};
			threadFactory.newThread(checkForUpdate).start();
		}
	}

	private static void setCurrentJob(final Job job) {
		currentJob = job;
	}

	private static void cancelCurrentJob() {
		if (currentJob != null) {
			currentJob.cancel();
			currentJob = null;
		}
	}

	private MomentTensorImageAsyncOperation() {}

}
