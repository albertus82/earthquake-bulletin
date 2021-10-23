package io.github.albertus82.eqbulletin.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import io.github.albertus82.util.IOUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Leaflet {

	public static final String LAYERS;

	private static final String RESOURCE_NAME = "leaflet-layers.js";

	static {
		String layers = null;
		try (final InputStream in = Leaflet.class.getResourceAsStream(RESOURCE_NAME); final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			IOUtils.copy(in, out, 1024);
			layers = out.toString(StandardCharsets.UTF_8.name());
		}
		catch (final Exception e) {
			log.error("Cannot read resource \"/" + Leaflet.class.getPackage().getName().replace('.', '/') + '/' + RESOURCE_NAME + "\":", e);
		}
		LAYERS = layers;
	}

}
