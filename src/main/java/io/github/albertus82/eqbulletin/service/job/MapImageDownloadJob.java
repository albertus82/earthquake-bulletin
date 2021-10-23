package io.github.albertus82.eqbulletin.service.job;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.eqbulletin.model.MapImage;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.eqbulletin.service.net.MapImageDownloader;

public class MapImageDownloadJob extends Job implements DownloadJob<MapImage> {

	private final Earthquake earthquake;

	private MapImage downloadedObject;

	private MapImageDownloader downloader;

	public MapImageDownloadJob(final Earthquake earthquake) {
		super(MapImageDownloadJob.class.getSimpleName());
		this.earthquake = earthquake;
		setUser(true);
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		try {
			downloader = new MapImageDownloader();
			downloadedObject = downloader.download(earthquake, monitor::isCanceled).orElse(null);
			monitor.done();
			return monitor.isCanceled() ? Status.CANCEL_STATUS : Status.OK_STATUS;
		}
		catch (final FileNotFoundException e) {
			return new Status(IStatus.INFO, getClass().getName(), Messages.get("error.job.map.not.found"), e);
		}
		catch (final IOException e) {
			return new Status(IStatus.WARNING, getClass().getName(), Messages.get("error.job.map"), e);
		}
		catch (final Exception e) {
			return new Status(IStatus.ERROR, getClass().getName(), Messages.get("error.job.map"), e);
		}
	}

	@Override
	public Optional<MapImage> getDownloadedObject() {
		return Optional.ofNullable(downloadedObject);
	}

	@Override
	protected void canceling() {
		if (downloader != null) {
			downloader.cancel();
		}
	}

}
