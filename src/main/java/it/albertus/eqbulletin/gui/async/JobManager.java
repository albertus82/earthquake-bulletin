package it.albertus.eqbulletin.gui.async;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class JobManager {

	static {
		System.setProperty(IJobManager.PROP_USE_DAEMON_THREADS, Boolean.TRUE.toString());
	}

	private static final AtomicInteger count = new AtomicInteger();

	public static void setWaitCursor(final Shell shell) {
		if (shell != null && !shell.isDisposed() && count.getAndIncrement() == 0) {
			shell.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_WAIT));
		}
	}

	public static void setDefaultCursor(final Shell shell) {
		if (shell != null && !shell.isDisposed() && count.decrementAndGet() == 0) {
			shell.setCursor(null);
		}
	}

	private JobManager() {
		throw new IllegalAccessError();
	}

}
