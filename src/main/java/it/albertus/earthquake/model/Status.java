package it.albertus.earthquake.model;

import it.albertus.earthquake.resources.Messages;

public enum Status {

	A,
	C,
	M;

	public String getDescription() {
		return Messages.get("lbl.status." + name().toLowerCase());
	}

}
