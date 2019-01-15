package it.albertus.eqbulletin.service;

public class CancelException extends Exception {

	private static final long serialVersionUID = 3215048578540929086L;

	public CancelException(final String message) {
		super(message);
	}

	public CancelException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
