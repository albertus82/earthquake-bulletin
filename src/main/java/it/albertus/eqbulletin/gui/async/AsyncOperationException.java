package it.albertus.eqbulletin.gui.async;

import java.util.function.BiConsumer;

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
		case IStatus.CANCEL:
			return log::info;
		default:
			return log::warn;
		}
	}

}
