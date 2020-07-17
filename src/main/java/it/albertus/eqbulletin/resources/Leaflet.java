package it.albertus.eqbulletin.resources;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.util.IOUtils;
import it.albertus.util.logging.LoggerFactory;

public class Leaflet {

	public static final String LAYERS;

	private static final String RESOURCE_NAME = "leaflet-layers.js";

	private static final Logger logger = LoggerFactory.getLogger(Leaflet.class);

	static {
		String layers = null;
		try (final InputStream in = Leaflet.class.getResourceAsStream(RESOURCE_NAME); final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			IOUtils.copy(in, out, 1024);
			layers = out.toString(StandardCharsets.UTF_8.name());
		}
		catch (final Exception e) {
			logger.log(Level.SEVERE, e, () -> "Cannot read resource \"/" + Leaflet.class.getPackage().getName().replace('.', '/') + '/' + RESOURCE_NAME + "\":");
		}
		LAYERS = layers;
	}

	private Leaflet() {
		throw new IllegalAccessError();
	}

}
