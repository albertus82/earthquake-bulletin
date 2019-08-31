package it.albertus.eqbulletin.resources;

import it.albertus.eqbulletin.config.ILanguageConfig;
import it.albertus.util.ILanguageManager;

public class LanguageManager implements ILanguageManager {

	private final ILanguageConfig languageConfig;

	public LanguageManager(final ILanguageConfig languageConfig, final boolean reset) {
		this.languageConfig = languageConfig;
		if (reset) {
			resetLanguage();
		}
	}

	@Override
	public void resetLanguage() {
		Messages.setLanguage(languageConfig.getLanguage());
	}

}
