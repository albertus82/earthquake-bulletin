package it.albertus.eqbulletin.service;

import java.io.IOException;

public class FetchException extends IOException {

	private static final long serialVersionUID = -3521045419884684567L;

	public FetchException(String message, Throwable cause) {
		super(message, cause);
	}

}
