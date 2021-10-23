package io.github.albertus82.eqbulletin.model;

import java.util.Locale;

import io.github.albertus82.eqbulletin.resources.Messages;

public enum Status {

	/** Automatic */
	A,
	/** Confirmed */
	C,
	/** Manually revised */
	M;

	public String getDescription() {
		return Messages.get("label.status." + name().toLowerCase(Locale.ROOT));
	}

}
