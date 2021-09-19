package it.albertus.eqbulletin.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum BuildInfo {

	INSTANCE;

	private static final String RESOURCE_NAME = "build-info.properties";

	final Properties properties = new Properties();

	private BuildInfo() {
		try (final InputStream is = getClass().getResourceAsStream(RESOURCE_NAME)) {
			if (is == null) {
				throw new FileNotFoundException(RESOURCE_NAME);
			}
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
		final String value = INSTANCE.properties.getProperty(key);
		if (value == null) {
			log.warn("Missing property for key: \"{}\".", key);
		}
		return value;
	}

}
