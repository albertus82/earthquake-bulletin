package com.github.albertus82.eqbulletin.resources;

import com.github.albertus82.eqbulletin.config.LanguageConfigAccessor;

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
