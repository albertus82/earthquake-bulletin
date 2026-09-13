package io.github.albertus82.eqbulletin.model;

import java.util.Locale;

import io.github.albertus82.eqbulletin.resources.Messages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Format {

	HTML("html"),
	RSS("rss"),
	QUAKEML(null);

	public static final String PARAM_NAME = "fmt";
	public static final Format DEFAULT = HTML;

	private final String paramValue;

	public String getLabel() {
		return Messages.get("label.form.format." + name().toLowerCase(Locale.ROOT));
	}

}
