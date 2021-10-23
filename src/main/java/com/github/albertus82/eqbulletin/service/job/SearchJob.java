package com.github.albertus82.eqbulletin.service.job;

import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.github.albertus82.eqbulletin.model.Bulletin;
import com.github.albertus82.eqbulletin.resources.Messages;
import com.github.albertus82.eqbulletin.service.BulletinProvider;
import com.github.albertus82.eqbulletin.service.SearchRequest;
import com.github.albertus82.eqbulletin.service.decode.DecodeException;
import com.github.albertus82.eqbulletin.service.net.FetchException;

import lombok.Setter;

public class SearchJob extends Job {

	private final SearchRequest request;
	private final BulletinProvider provider;

	@Setter
	private volatile boolean canceled;

	private Bulletin bulletin;

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
			bulletin = provider.getBulletin(request, monitor::isCanceled).orElse(null);
			monitor.done();
			return Status.OK_STATUS;
		}
		catch (final FetchException e) {
			return new Status(IStatus.WARNING, getClass().getName(), Messages.get("error.job.fetch"), e.getCause() != null ? e.getCause() : e);
		}
		catch (final DecodeException e) {
			return new Status(IStatus.WARNING, getClass().getName(), Messages.get("error.job.decode"), e.getCause() != null ? e.getCause() : e);
		}
		catch (final Exception | LinkageError e) {
			return new Status(IStatus.ERROR, getClass().getName(), Messages.get("error.unexpected"), e);
		}
	}

	public Optional<Bulletin> getBulletin() {
		return Optional.ofNullable(bulletin);
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

}
