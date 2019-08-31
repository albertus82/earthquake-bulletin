package it.albertus.eqbulletin.config;

import java.util.Locale;

import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.jface.preference.IPreferencesConfiguration;

public class LanguageConfig implements ILanguageConfig {

	public static final String DEFAULT_LANGUAGE = Locale.getDefault().getLanguage();

	private IPreferencesConfiguration configuration;

	LanguageConfig(final IPreferencesConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public String getLanguage() {
		return configuration.getString(Preference.LANGUAGE, DEFAULT_LANGUAGE);
	}

}
