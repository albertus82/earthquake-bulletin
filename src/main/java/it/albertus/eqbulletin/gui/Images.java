package it.albertus.eqbulletin.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import it.albertus.util.logging.LoggerFactory;

public class Images {

	private static final Logger logger = LoggerFactory.getLogger(Images.class);

	private static final Comparator<Rectangle> areaComparator = (r1, r2) -> {
		final int a1 = r1.width * r1.height;
		final int a2 = r2.width * r2.height;
		if (a1 > a2) {
			return -1;
		}
		if (a1 < a2) {
			return 1;
		}
		return 0;
	};

	// Main application icon in various formats, sorted by size (area) descending.
	private static final Map<Rectangle, Image> mainIconMap = new TreeMap<>(areaComparator);

	private static final Map<Rectangle, Image> openStreetMapIconMap = new TreeMap<>(areaComparator);

	private Images() {
		throw new IllegalAccessError();
	}

	static {
		try (final InputStream stream = Images.class.getResourceAsStream("main.ico")) {
			for (final ImageData data : new ImageLoader().load(stream)) {
				final Image image = new Image(Display.getCurrent(), data);
				mainIconMap.put(image.getBounds(), image);
			}
		}
		catch (final IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		logger.log(Level.CONFIG, "Main icons: {0}", mainIconMap);

		try (final InputStream stream = Images.class.getResourceAsStream("osm.ico")) {
			for (final ImageData data : new ImageLoader().load(stream)) {
				final Image image = new Image(Display.getCurrent(), data);
				openStreetMapIconMap.put(image.getBounds(), image);
			}
		}
		catch (final IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
		logger.log(Level.CONFIG, "OpenStreetMap icons: {0}", openStreetMapIconMap);
	}

	public static Image[] getMainIconArray() {
		return getMainIconMap().values().toArray(new Image[0]);
	}

	public static Map<Rectangle, Image> getMainIconMap() {
		return Collections.unmodifiableMap(mainIconMap);
	}

	public static Image[] getOpenStreetMapIconArray() {
		return getOpenStreetMapIconMap().values().toArray(new Image[0]);
	}

	public static Map<Rectangle, Image> getOpenStreetMapIconMap() {
		return Collections.unmodifiableMap(openStreetMapIconMap);
	}

}
