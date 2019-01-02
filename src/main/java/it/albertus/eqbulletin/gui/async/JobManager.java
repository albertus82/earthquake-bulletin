package it.albertus.eqbulletin.gui.async;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.swt.widgets.Shell;

import it.albertus.jface.SwtUtils;

public class JobManager {

	static {
		System.setProperty(IJobManager.PROP_USE_DAEMON_THREADS, Boolean.TRUE.toString());
	}

	private static final AtomicInteger jobsCount = new AtomicInteger();

	public static void setWaitCursor(final Shell shell) {
		if (jobsCount.getAndIncrement() == 0) {
			SwtUtils.setWaitCursor(shell);
		}
	}

	public static void setDefaultCursor(final Shell shell) {
		if (jobsCount.decrementAndGet() == 0) {
			SwtUtils.setDefaultCursor(shell);
		}
	}

	private JobManager() {
		throw new IllegalAccessError();
	}

}
