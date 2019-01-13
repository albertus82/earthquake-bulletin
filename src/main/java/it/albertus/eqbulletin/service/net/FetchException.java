package it.albertus.eqbulletin.service.net;

import java.io.IOException;

public class FetchException extends IOException {

	private static final long serialVersionUID = -3521045419884684567L;

	public FetchException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
