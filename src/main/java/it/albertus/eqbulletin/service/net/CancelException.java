package it.albertus.eqbulletin.service.net;

import lombok.NoArgsConstructor;

@NoArgsConstructor
class CancelException extends Exception {

	private static final long serialVersionUID = -5082561084350306737L;

	CancelException(final Throwable cause) {
		super(cause);
	}

	CancelException(final String message) {
		super(message);
	}

}
