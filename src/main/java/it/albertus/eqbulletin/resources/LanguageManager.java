package it.albertus.eqbulletin.resources;

import it.albertus.eqbulletin.config.LanguageConfigAccessor;
import it.albertus.util.ILanguageManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LanguageManager implements ILanguageManager {

	@NonNull
	private final LanguageConfigAccessor languageConfig;

	@Override
	public void resetLanguage() {
		Messages.setLanguage(languageConfig.getLanguage());
	}

}
