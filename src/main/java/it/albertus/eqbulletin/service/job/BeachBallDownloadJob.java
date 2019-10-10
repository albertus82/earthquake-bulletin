package it.albertus.eqbulletin.service.job;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.BeachBall;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.net.BeachBallDownloader;

public class BeachBallDownloadJob extends Job implements DownloadJob<BeachBall> {

	private final Earthquake earthquake;

	private Optional<BeachBall> downloadedObject = Optional.empty();

	private BeachBallDownloader downloader;

	public BeachBallDownloadJob(final Earthquake earthquake) {
		super(BeachBallDownloadJob.class.getSimpleName());
		this.earthquake = earthquake;
		setUser(true);
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		try {
			downloader = new BeachBallDownloader();
			downloadedObject = downloader.download(earthquake, monitor::isCanceled);
			monitor.done();
			return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
		}
		catch (final FileNotFoundException e) {
			return new Status(IStatus.INFO, getClass().getName(), Messages.get("err.job.mtimage.not.found"), e);
		}
		catch (final IOException e) {
			return new Status(IStatus.WARNING, getClass().getName(), Messages.get("err.job.mtimage"), e);
		}
		catch (final Exception e) {
			return new Status(IStatus.ERROR, getClass().getName(), Messages.get("err.job.mtimage"), e);
		}
	}

	@Override
	public Optional<BeachBall> getDownloadedObject() {
		return downloadedObject;
	}

	@Override
	protected void canceling() {
		if (downloader != null) {
			downloader.cancel();
		}
	}

}
