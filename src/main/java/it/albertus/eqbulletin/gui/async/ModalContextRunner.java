package it.albertus.eqbulletin.gui.async;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Display;

public class ModalContextRunner {

	static {
		System.setProperty(IJobManager.PROP_USE_DAEMON_THREADS, Boolean.TRUE.toString());
	}

	private final Job job;
	private long timeoutMillis = 0;
	private boolean daemon = true;

	public static void run(final Job job, final Display display) throws OperationException {
		try {
			new ModalContextRunner(job).run(display);
		}
		catch (final TimeoutException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void run(final Job job, final Display display, final long timeoutMillis, final boolean daemonThread) throws OperationException, TimeoutException {
		final ModalContextRunner modalContextRunner = new ModalContextRunner(job);
		modalContextRunner.setTimeoutMillis(timeoutMillis);
		modalContextRunner.setDaemon(daemonThread);
		modalContextRunner.run(display);
	}

	private ModalContextRunner(final Job job) {
		this.job = job;
	}

	private void setTimeoutMillis(final long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	private void setDaemon(final boolean daemon) {
		this.daemon = daemon;
	}

	public void run(final Display display) throws OperationException, TimeoutException {
		job.schedule();
		try {
			ModalContext.run(new JoinOperation(), true, new NullProgressMonitor(), display);
		}
		catch (final InvocationTargetException | InterruptedException e) { // NOSONAR
			throw new IllegalStateException(e);
		}
		if (job.getResult() == null || job.getState() == Job.RUNNING) {
			job.cancel();
			final Thread thread = job.getThread();
			if (thread != null) {
				thread.interrupt();
			}
			throw new TimeoutException();
		}
		if (job.getResult().matches(IStatus.ERROR | IStatus.WARNING)) {
			throw new OperationException(job.getResult());
		}
	}

	private class JoinOperation implements IThreadListener, IRunnableWithProgress {
		@Override
		public void run(final IProgressMonitor monitor) throws InterruptedException {
			if (timeoutMillis == 0) {
				job.join();
			}
			else {
				job.join(timeoutMillis, monitor);
			}
		}

		@Override
		public void threadChange(final Thread thread) {
			if (daemon && !thread.isAlive()) {
				thread.setDaemon(daemon);
			}
		}
	}

}
