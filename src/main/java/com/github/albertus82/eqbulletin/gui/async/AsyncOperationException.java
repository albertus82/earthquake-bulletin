package com.github.albertus82.eqbulletin.gui.async;

import org.eclipse.core.runtime.IStatus;
import org.slf4j.Logger;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class AsyncOperationException extends Exception {

	private static final long serialVersionUID = -4994870282700071908L;

	private final int severity;

	public AsyncOperationException(@NonNull final IStatus status) {
		super(status.getMessage(), status.getException());
		this.severity = status.getSeverity();
	}

	public void log(@NonNull final Logger log) {
		switch (severity) {
		case IStatus.OK:
			log.debug(getMessage(), this);
			break;
		case IStatus.INFO:
			log.info(getMessage(), this);
			break;
		case IStatus.WARNING:
			log.warn(getMessage(), this);
			break;
		case IStatus.ERROR:
			log.error(getMessage(), this);
			break;
		case IStatus.CANCEL:
			log.info(getMessage(), this);
			break;
		default:
			log.warn(getMessage(), this);
			break;
		}
	}

}
