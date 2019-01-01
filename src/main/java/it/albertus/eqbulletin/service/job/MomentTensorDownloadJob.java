package it.albertus.eqbulletin.service.job;

import java.io.IOException;

import org.eclipse.core.internal.jobs.JobStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.net.MomentTensorDownloader;

public class MomentTensorDownloadJob extends Job {

	private static final String NAME = "Download moment tensor";

	private final Earthquake earthquake;

	private MomentTensor momentTensor; // The result.

	public MomentTensorDownloadJob(final Earthquake earthquake) {
		super(NAME);
		this.earthquake = earthquake;
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(NAME, IProgressMonitor.UNKNOWN);
		try {
			momentTensor = new MomentTensorDownloader().download(earthquake);
			monitor.done();
			return JobStatus.OK_STATUS;
		}
		catch (final IOException e) {
			return new Status(IStatus.WARNING, getClass().getName(), Messages.get("err.job.mt.show"), e);
		}
		catch (final Exception e) {
			return new Status(IStatus.ERROR, getClass().getName(), Messages.get("err.job.mt.show"), e);
		}
	}

	public MomentTensor getMomentTensor() {
		return momentTensor;
	}

}
