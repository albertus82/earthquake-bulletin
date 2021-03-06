package it.albertus.eqbulletin.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import it.albertus.util.IOUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
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
			log.log(Level.SEVERE, e, () -> "Cannot read resource \"/" + Leaflet.class.getPackage().getName().replace('.', '/') + '/' + RESOURCE_NAME + "\":");
		}
		LAYERS = layers;
	}

}
