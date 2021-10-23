package io.github.albertus82.eqbulletin.service.decode.html;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlBulletinDecoderFactory {

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	public static HtmlBulletinDecoder newInstance() {
		final String version = configuration.getString(Preference.HTML_BULLETIN_VERSION);
		switch (HtmlBulletinVersion.forValue(version)) {
		case NEW:
			return new NewHtmlBulletinDecoder();
		case OLD:
			return new OldHtmlBulletinDecoder();
		default:
			throw new IllegalArgumentException(version);
		}
	}

}
