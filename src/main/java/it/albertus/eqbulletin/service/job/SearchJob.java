package it.albertus.eqbulletin.service.job;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.BulletinProvider;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.net.FetchException;

public class SearchJob extends Job {

	private final SearchRequest request;
	private final BulletinProvider provider;

	private volatile boolean canceled;

	private Optional<Collection<Earthquake>> earthquakes = Optional.empty();

	public SearchJob(final SearchRequest request, final BulletinProvider provider) {
		super(SearchJob.class.getSimpleName());
		this.request = request;
		this.provider = provider;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		try {
			earthquakes = provider.getEarthquakes(request, monitor::isCanceled);
			monitor.done();
			return Status.OK_STATUS;
		}
		catch (final FetchException | DecodeException e) {
			return new Status(IStatus.WARNING, getClass().getName(), e.getMessage(), e.getCause() != null ? e.getCause() : e);
		}
		catch (final Exception | LinkageError e) {
			return new Status(IStatus.ERROR, getClass().getName(), e.toString(), e);
		}
	}

	public Optional<Collection<Earthquake>> getEarthquakes() {
		return earthquakes;
	}

	@Override
	protected void canceling() {
		if (provider != null) {
			provider.cancel();
		}
	}

	@Override
	public boolean shouldSchedule() {
		return !canceled;
	}

	@Override
	public boolean shouldRun() {
		return !canceled;
	}

	public void setCanceled(final boolean canceled) {
		this.canceled = canceled;
	}

}
