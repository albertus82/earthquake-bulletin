package it.albertus.eqbulletin.service.job;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.BulletinProvider;
import it.albertus.eqbulletin.service.GeofonBulletinProvider;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.net.FetchException;

public class SearchJob extends Job {

	private final SearchRequest request;

	private BulletinProvider provider;
	private volatile boolean canceled;

	private Collection<Earthquake> earthquakes;

	public SearchJob(final SearchRequest request) {
		super(SearchJob.class.getSimpleName());
		this.request = request;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);

		try {
			provider = new GeofonBulletinProvider();
			earthquakes = provider.getEarthquakes(request, monitor::isCanceled);
		}
		catch (final InterruptedException e) { // NOSONAR
			return new Status(IStatus.INFO, getClass().getName(), "Job was canceled.", e);
		}
		catch (final FetchException | DecodeException e) {
			return new Status(IStatus.WARNING, getClass().getName(), e.getMessage(), e.getCause() != null ? e.getCause() : e);
		}
		catch (final Exception | LinkageError e) {
			return new Status(IStatus.ERROR, getClass().getName(), e.toString(), e);
		}

		monitor.done();
		return Status.OK_STATUS;
	}

	public Collection<Earthquake> getEarthquakes() {
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
