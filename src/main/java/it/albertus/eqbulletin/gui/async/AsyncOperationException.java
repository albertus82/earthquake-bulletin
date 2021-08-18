package it.albertus.eqbulletin.gui.async;

import java.util.function.BiConsumer;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class AsyncOperationException extends Exception {

	private static final long serialVersionUID = 1940542061335327247L;

	private final int severity;

	public AsyncOperationException(final IStatus status) {
		super(status.getMessage(), status.getException());
		this.severity = status.getSeverity();
	}

	public AsyncOperationException(final String message, final Throwable cause) {
		super(message, cause);
		this.severity = IStatus.ERROR;
	}

	public BiConsumer<String, Throwable> getLoggingMethod(@NonNull final Logger log) {
		switch (severity) {
		case IStatus.OK:
			return log::debug;
		case IStatus.INFO:
			return log::info;
		case IStatus.WARNING:
			return log::warn;
		case IStatus.ERROR:
			return log::error;
		default:
			return log::warn;
		}
	}

	public int getSystemImageId() {
		switch (severity) {
		case IStatus.OK:
			return SWT.ICON_INFORMATION;
		case IStatus.INFO:
			return SWT.ICON_INFORMATION;
		case IStatus.WARNING:
			return SWT.ICON_WARNING;
		case IStatus.ERROR:
			return SWT.ICON_ERROR;
		case IStatus.CANCEL:
			return SWT.ICON_CANCEL;
		default:
			return SWT.ICON_WARNING;
		}
	}

}
