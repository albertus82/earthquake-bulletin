package com.github.albertus82.eqbulletin.model;

import java.util.Locale;

import com.github.albertus82.eqbulletin.resources.Messages;

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
		return Messages.get("label.form.format." + name().toLowerCase(Locale.ROOT));
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
