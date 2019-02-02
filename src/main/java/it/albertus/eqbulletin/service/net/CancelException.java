package it.albertus.eqbulletin.service.net;

class CancelException extends Exception {

	private static final long serialVersionUID = -5082561084350306737L;

	CancelException() {}

	CancelException(final Throwable cause) {
		super(cause);
	}

	CancelException(final String message) {
		super(message);
	}

}
