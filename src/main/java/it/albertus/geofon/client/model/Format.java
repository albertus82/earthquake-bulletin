package it.albertus.geofon.client.model;

import it.albertus.geofon.client.resources.Messages;

public enum Format {
	RSS,
	KML;

	public String getDescription() {
		return Messages.get("lbl.form.format." + name().toLowerCase());
	}

}
