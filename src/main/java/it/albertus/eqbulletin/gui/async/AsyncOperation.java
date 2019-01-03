package it.albertus.eqbulletin.gui.async;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.swt.widgets.Shell;

import it.albertus.jface.SwtUtils;
import it.albertus.util.DaemonThreadFactory;
import it.albertus.util.logging.LoggerFactory;

public abstract class AsyncOperation<T> {

	private static final Logger logger = LoggerFactory.getLogger(AsyncOperation.class);

	protected static final ThreadFactory threadFactory = new DaemonThreadFactory() {
		@Override
		public Thread newThread(final Runnable r) {
			final Thread thread = super.newThread(r);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.setUncaughtExceptionHandler((t, e) -> logger.log(Level.SEVERE, t.toString(), e));
			return thread;
		}
	};

	private static final AtomicInteger operationCount = new AtomicInteger();

	static {
		System.setProperty(IJobManager.PROP_USE_DAEMON_THREADS, Boolean.TRUE.toString());
	}

	public abstract void execute(T arg, Shell shell);

	protected void setWaitCursor(final Shell shell) {
		logger.log(Level.FINE, "setWaitCursor() - operationCount = {0}", operationCount);
		if (operationCount.getAndIncrement() == 0) {
			SwtUtils.setWaitCursor(shell);
		}
	}

	protected void setDefaultCursor(final Shell shell) {
		if (operationCount.updateAndGet(o -> o > 1 ? o - 1 : 0) == 0) {
			SwtUtils.setDefaultCursor(shell);
		}
		logger.log(Level.FINE, "setDefaultCursor() - operationCount = {0}", operationCount);
	}

}
