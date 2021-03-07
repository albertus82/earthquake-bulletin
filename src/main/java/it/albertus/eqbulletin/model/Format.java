package it.albertus.eqbulletin.model;

import it.albertus.eqbulletin.resources.Messages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Format {

	HTML("html"),
	RSS("rss");

	public static final String KEY = "fmt";
	public static final Format DEFAULT = HTML;

	private final String value;

	public String getLabel() {
		return Messages.get("lbl.form.format." + name().toLowerCase());
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
