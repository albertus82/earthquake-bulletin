package it.albertus.geofon.client.model;

import it.albertus.geofon.client.resources.Messages;

public enum Format {
	RSS("rss"),
	XHTML("html");

	private final String value;

	Format(final String value) {
		this.value = value;
	}

	public String getLabel() {
		return Messages.get("lbl.form.format." + name().toLowerCase());
	}

	public String getValue() {
		return value;
	}

}
