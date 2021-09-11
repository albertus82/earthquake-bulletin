package it.albertus.eqbulletin.model;

import java.util.Locale;

import it.albertus.eqbulletin.resources.Messages;

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
