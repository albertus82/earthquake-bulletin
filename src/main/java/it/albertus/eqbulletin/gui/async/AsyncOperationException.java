package it.albertus.eqbulletin.gui.async;

import java.util.logging.Level;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;

import lombok.Getter;

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

	public Level getLoggingLevel() {
		switch (severity) {
		case IStatus.OK:
			return Level.FINE;
		case IStatus.INFO:
			return Level.INFO;
		case IStatus.WARNING:
			return Level.WARNING;
		case IStatus.ERROR:
			return Level.SEVERE;
		default:
			return Level.WARNING;
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
