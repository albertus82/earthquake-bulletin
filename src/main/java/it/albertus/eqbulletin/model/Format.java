package it.albertus.eqbulletin.model;

import it.albertus.eqbulletin.resources.Messages;

public enum Format {

	HTML("html"),
	RSS("rss");

	public static final String KEY = "fmt";
	public static final Format DEFAULT = HTML;

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

	public static Format forValue(final String value) {
		if (value != null) {
			for (final Format format : values()) {
				if (value.equals(format.getValue())) {
					return format;
				}
			}
		}
		return DEFAULT;
	}

}
