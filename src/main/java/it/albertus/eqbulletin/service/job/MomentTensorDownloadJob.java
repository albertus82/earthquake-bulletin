package it.albertus.eqbulletin.service.job;

import java.io.FileNotFoundException;
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

public class MomentTensorDownloadJob extends Job implements DownloadJob<MomentTensor> {

	private final Earthquake earthquake;

	private MomentTensor downloadedObject;

	public MomentTensorDownloadJob(final Earthquake earthquake) {
		super(MomentTensorDownloadJob.class.getSimpleName());
		this.earthquake = earthquake;
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask(getName(), IProgressMonitor.UNKNOWN);
		try {
			downloadedObject = new MomentTensorDownloader().download(earthquake);
			monitor.done();
			return JobStatus.OK_STATUS;
		}
		catch (final FileNotFoundException e) {
			return new Status(IStatus.INFO, getClass().getName(), Messages.get("err.job.mt.not.found"), e);
		}
		catch (final IOException e) {
			return new Status(IStatus.WARNING, getClass().getName(), Messages.get("err.job.mt"), e);
		}
		catch (final Exception e) {
			return new Status(IStatus.ERROR, getClass().getName(), Messages.get("err.job.mt"), e);
		}
	}

	@Override
	public MomentTensor getDownloadedObject() {
		return downloadedObject;
	}

}
