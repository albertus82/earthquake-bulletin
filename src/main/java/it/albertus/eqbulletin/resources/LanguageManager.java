package it.albertus.eqbulletin.resources;

import it.albertus.eqbulletin.config.ILanguageConfig;
import it.albertus.util.ILanguageManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LanguageManager implements ILanguageManager {

	private final ILanguageConfig languageConfig;

	@Override
	public void resetLanguage() {
		Messages.setLanguage(languageConfig.getLanguage());
	}

}
