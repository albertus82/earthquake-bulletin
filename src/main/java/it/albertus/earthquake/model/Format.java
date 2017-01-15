package it.albertus.earthquake.model;

import it.albertus.earthquake.resources.Messages;

public enum Format {
	HTML("html"),
	RSS("rss");

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
