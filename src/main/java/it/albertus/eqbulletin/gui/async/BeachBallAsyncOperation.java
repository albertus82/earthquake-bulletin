package it.albertus.eqbulletin.gui.async;

import java.io.Serializable;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import it.albertus.eqbulletin.cache.BeachBallCache;
import it.albertus.eqbulletin.model.BeachBall;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.job.BeachBallDownloadJob;
import it.albertus.eqbulletin.service.net.BeachBallDownloader;
import it.albertus.util.logging.LoggerFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeachBallAsyncOperation extends AsyncOperation {

	private static final Logger logger = LoggerFactory.getLogger(BeachBallAsyncOperation.class);

	private static Job currentJob;

	public static synchronized void execute(final Earthquake earthquake) {
		if (earthquake != null && earthquake.getMomentTensorUri().isPresent()) {
			cancelCurrentJob();
			final BeachBallCache cache = BeachBallCache.getInstance();
			final String guid = earthquake.getGuid();
			final BeachBall cachedObject = cache.get(guid);
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

	private static void cacheHit(final BeachBall cachedObject, final Earthquake earthquake) {
		checkForUpdateAndRefreshIfNeeded(cachedObject, earthquake);
	}

	private static void cacheMiss(final Earthquake earthquake) {
		final BeachBallDownloadJob job = new BeachBallDownloadJob(earthquake);
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				try {
					if (!event.getResult().isOK() && event.getResult().getSeverity() != IStatus.CANCEL) {
						throw new AsyncOperationException(job.getResult());
					}
					final Optional<BeachBall> downloadedObject = job.getDownloadedObject();
					if (downloadedObject.isPresent()) {
						BeachBallCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
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

	private static void checkForUpdateAndRefreshIfNeeded(final BeachBall cachedObject, final Earthquake earthquake) {
		if (cachedObject.getEtag() != null && !cachedObject.getEtag().trim().isEmpty()) {
			final Runnable checkForUpdate = () -> {
				try {
					final Optional<BeachBall> downloadedObject = new BeachBallDownloader().download(earthquake, cachedObject, () -> false);
					if (downloadedObject.isPresent() && !downloadedObject.get().equals(cachedObject)) {
						BeachBallCache.getInstance().put(earthquake.getGuid(), downloadedObject.get());
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

}
