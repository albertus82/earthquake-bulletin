package io.github.albertus82.eqbulletin.resources;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import io.github.albertus82.jface.JFaceMessages;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Messages {

	public enum Language {
		ENGLISH(Locale.ENGLISH),
		ITALIAN(Locale.ITALIAN);

		private final Locale locale;

		private Language(final Locale locale) {
			this.locale = locale;
		}

		public Locale getLocale() {
			return locale;
		}
	}

	private static final String BASE_NAME = Messages.class.getName().toLowerCase(Locale.ROOT);

	private static ResourceBundle resources = ResourceBundle.getBundle(BASE_NAME, ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

	/** Aggiorna la lingua in cui vengono mostrati i messaggi. */
	static void setLanguage(final String language) {
		if (language != null) {
			resources = ResourceBundle.getBundle(BASE_NAME, new Locale(language), ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
			JFaceMessages.setLanguage(language);
		}
	}

	public static Language getLanguage() {
		for (final Language language : Language.values()) {
			if (language.locale.equals(resources.getLocale())) {
				return language;
			}
		}
		return Language.ENGLISH; // Default.
	}

	public static String get(final String key) {
		String message;
		try {
			message = resources.getString(key);
			message = message != null ? message.replace("''", "'").trim() : "";
		}
		catch (final MissingResourceException e) {
			message = JFaceMessages.get(key);
		}
		return message;
	}

	public static String get(final String key, final Object... params) {
		final List<String> stringParams = new ArrayList<>(params.length);
		for (final Object param : params) {
			stringParams.add(String.valueOf(param));
		}
		String message;
		try {
			message = MessageFormat.format(resources.getString(key), stringParams.toArray());
			message = message != null ? message.trim() : "";
		}
		catch (final MissingResourceException e) {
			message = JFaceMessages.get(key, params);
		}
		return message;
	}

	public static Collection<String> getKeys() {
		final Collection<String> keys = new HashSet<>(JFaceMessages.getKeys());
		if (resources != null) {
			keys.addAll(Collections.list(resources.getKeys()));
		}
		return keys;
	}

}
