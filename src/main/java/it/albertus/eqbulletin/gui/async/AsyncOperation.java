package it.albertus.eqbulletin.gui.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.swt.widgets.Shell;

import it.albertus.jface.SwtUtils;
import it.albertus.util.DaemonThreadFactory;

public abstract class AsyncOperation<T> {

	protected static final ThreadFactory threadFactory = new DaemonThreadFactory();

	private static final AtomicInteger jobsCount = new AtomicInteger();

	static {
		System.setProperty(IJobManager.PROP_USE_DAEMON_THREADS, Boolean.TRUE.toString());
	}

	public abstract void execute(T arg, Shell shell);

	protected void setWaitCursor(final Shell shell) {
		if (jobsCount.getAndIncrement() == 0) {
			SwtUtils.setWaitCursor(shell);
		}
	}

	protected void setDefaultCursor(final Shell shell) {
		if (jobsCount.decrementAndGet() == 0) {
			SwtUtils.setDefaultCursor(shell);
		}
	}

}
