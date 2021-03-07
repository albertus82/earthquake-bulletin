package it.albertus.eqbulletin.service.decode.html;

import it.albertus.eqbulletin.resources.Messages;

public enum HtmlBulletinVersion {

	OLD,
	NEW;

	public static final HtmlBulletinVersion DEFAULT = OLD;

	public String getLabel() {
		return Messages.get("lbl.html.bulletin.version." + name().toLowerCase());
	}

	public static HtmlBulletinVersion forValue(final String value) {
		if (value != null) {
			for (final HtmlBulletinVersion version : values()) {
				if (value.trim().equalsIgnoreCase(version.name())) {
					return version;
				}
			}
		}
		return DEFAULT;
	}

}
