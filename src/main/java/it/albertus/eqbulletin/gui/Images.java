package it.albertus.eqbulletin.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import it.albertus.util.logging.LoggerFactory;

public class Images {

	private static final Logger logger = LoggerFactory.getLogger(Images.class);

	private static final Comparator<Rectangle> areaComparatorDescending = (r1, r2) -> {
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

	private static final Map<Rectangle, Image> appIconMap;
	private static final Map<Rectangle, Image> mapIconMap;

	static {
		appIconMap = loadFromResource(Images.class.getPackage().getName() + ".icon.app");
		mapIconMap = loadFromResource(Images.class.getPackage().getName() + ".icon.map");
	}

	private static Map<Rectangle, Image> loadFromResource(final String resourceName) {
		final Reflections reflections = new Reflections(resourceName, new ResourcesScanner());
		final Iterable<String> fileNames = reflections.getResources(Pattern.compile(".*\\.png"));

		final Map<Rectangle, Image> map = new TreeMap<>(areaComparatorDescending);
		for (final String fileName : fileNames) {
			try (final InputStream stream = Images.class.getResourceAsStream('/' + fileName)) {
				for (final ImageData data : new ImageLoader().load(stream)) {
					final Image image = new Image(Display.getCurrent(), data);
					map.put(image.getBounds(), image);
				}
			}
			catch (final IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		logger.log(Level.CONFIG, "{0}: {1}", new Object[] { resourceName, map });
		return map;
	}

	public static Image[] getAppIconArray() {
		return getAppIconMap().values().toArray(new Image[0]);
	}

	/**
	 * Main application icon in various formats, sorted by size (area)
	 * <b>descending</b>.
	 */
	public static Map<Rectangle, Image> getAppIconMap() {
		return Collections.unmodifiableMap(appIconMap);
	}

	public static Image[] getMapIconArray() {
		return getMapIconMap().values().toArray(new Image[0]);
	}

	/** Map icon in various formats, sorted by size (area) <b>descending</b>. */
	public static Map<Rectangle, Image> getMapIconMap() {
		return Collections.unmodifiableMap(mapIconMap);
	}

	private Images() {
		throw new IllegalAccessError();
	}

}
