package it.albertus.eqbulletin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

import lombok.NonNull;

public enum BuildInfo {

	INSTANCE;

	private final Properties properties = new Properties();

	private BuildInfo() {
		try (final InputStream is = getClass().getResourceAsStream("/META-INF/build-info.properties")) {
			properties.load(is);
		}
		catch (final IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static String getProperty(@NonNull final String key) {
		return INSTANCE.properties.getProperty(key);
	}

}