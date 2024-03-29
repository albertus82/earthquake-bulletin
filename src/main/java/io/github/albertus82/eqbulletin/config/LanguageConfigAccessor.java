package io.github.albertus82.eqbulletin.config;

import java.util.Locale;

import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.albertus82.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LanguageConfigAccessor {

	public static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage();

	@NonNull
	private final IPreferencesConfiguration configuration;

	public String getLanguage() {
		return configuration.getString(Preference.LANGUAGE, DEFAULT_LANGUAGE);
	}

}
