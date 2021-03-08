package it.albertus.eqbulletin.model;

import it.albertus.eqbulletin.resources.Messages;

public enum Status {

	A,
	C,
	M;

	public String getDescription() {
		return Messages.get("label.status." + name().toLowerCase());
	}

}
