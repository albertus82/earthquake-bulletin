package it.albertus.eqbulletin;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.util.logging.LoggingSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EarthquakeBulletin {

	public static final String ARTIFACT_ID = "earthquake-bulletin";

	static {
		if (LoggingSupport.getFormat() == null) {
			LoggingSupport.setFormat(LoggingSupport.DEFAULT_FORMAT);
		}
	}

	public static void main(final String[] args) {
		EarthquakeBulletinGui.main();
	}

}
