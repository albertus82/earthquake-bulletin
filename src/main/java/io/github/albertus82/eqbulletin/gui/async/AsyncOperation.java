package io.github.albertus82.eqbulletin.gui.async;

import static io.github.albertus82.jface.DisplayThreadExecutor.Mode.ASYNC;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import io.github.albertus82.eqbulletin.gui.EarthquakeBulletinGui;
import io.github.albertus82.eqbulletin.gui.Images;
import io.github.albertus82.jface.DisplayThreadExecutor;
import io.github.albertus82.jface.EnhancedErrorDialog;
import io.github.albertus82.util.DaemonThreadFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AsyncOperation {

	protected static final ThreadFactory threadFactory = new DaemonThreadFactory() {
		@Override
		public Thread newThread(final Runnable r) {
			final Thread thread = super.newThread(r);
			thread.setPriority(Thread.MIN_PRIORITY);
			thread.setUncaughtExceptionHandler((t, e) -> log.error("An unrecoverable error has occurred in a secondary thread:", e));
			return thread;
		}
	};

	private static final AtomicInteger operationCount = new AtomicInteger();

	static {
		System.setProperty(IJobManager.PROP_USE_DAEMON_THREADS, Boolean.TRUE.toString());
	}

	protected static void setAppStartingCursor(final Shell shell) {
		log.debug("setAppStartingCursor() - operationCount = {}", operationCount);
		if (operationCount.getAndIncrement() == 0 && shell != null && !shell.isDisposed()) {
			shell.setCursor(shell.getDisplay().getSystemCursor(SWT.CURSOR_APPSTARTING));
		}
	}

	protected static void setDefaultCursor(final Shell shell) {
		if (operationCount.updateAndGet(o -> o > 1 ? o - 1 : 0) == 0 && shell != null && !shell.isDisposed()) {
			shell.setCursor(null);
		}
		log.debug("setDefaultCursor() - operationCount = {}", operationCount);
	}

	protected static void showErrorDialog(@NonNull final AsyncOperationException e, final Shell shell) {
		if (shell != null && !shell.isDisposed()) {
			new DisplayThreadExecutor(shell, ASYNC).execute(() -> EnhancedErrorDialog.openError(shell, EarthquakeBulletinGui.getApplicationName(), e.getMessage(), e.getSeverity(), e.getCause() != null ? e.getCause() : e, Images.getAppIconArray()));
		}
	}

}
