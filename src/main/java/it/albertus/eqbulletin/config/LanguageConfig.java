package it.albertus.eqbulletin.config;

import java.util.Locale;

import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class LanguageConfig implements ILanguageConfig {

	public static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage();

	@NonNull
	private final IPreferencesConfiguration configuration;

	@Override
	public String getLanguage() {
		return configuration.getString(Preference.LANGUAGE, DEFAULT_LANGUAGE);
	}

}
