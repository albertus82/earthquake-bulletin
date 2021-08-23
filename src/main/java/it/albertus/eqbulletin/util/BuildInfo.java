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

	/**
	 * Searches for the property with the specified key in this property list. The
	 * method returns {@code null} if the property is not found.
	 *
	 * @param key the property key.
	 * @return the value in this property list with the specified key value.
	 */
	public static String getProperty(@NonNull final String key) {
		return INSTANCE.properties.getProperty(key);
	}

}
