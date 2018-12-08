package it.albertus.eqbulletin.util;

public class InitializationException extends RuntimeException {

	private static final long serialVersionUID = -6188963042446285079L;

	public InitializationException(final String message) {
		super(message);
	}

	public InitializationException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
