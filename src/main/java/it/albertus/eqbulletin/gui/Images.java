package it.albertus.eqbulletin.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.swing.SortOrder;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import it.albertus.jface.ImageUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Images {

	/**
	 * Main application icon in various formats, sorted by size (area)
	 * <b>descending</b>.
	 */
	@Getter private static final Map<Rectangle, Image> appIconMap = Collections.unmodifiableMap(loadFromResources(Images.class.getPackage().getName() + ".icon.app"));

	private static Map<Rectangle, Image> loadFromResources(final String packageName) {
		final Reflections reflections = new Reflections(packageName, new ResourcesScanner());
		final Iterable<String> resourceNames = reflections.getResources(name -> name.toLowerCase(Locale.ROOT).endsWith(".png")).stream().map(name -> '/' + name).collect(Collectors.toSet());
		final Map<Rectangle, Image> map = ImageUtils.createImageMap(resourceNames, SortOrder.DESCENDING);
		log.log(Level.CONFIG, "{0}: {1}", new Object[] { packageName, map });
		return map;
	}

	public static Image[] getAppIconArray() {
		return appIconMap.values().toArray(new Image[0]);
	}

	public static Image getMapIcon(final int desiredSizePixels) {
		final Reflections reflections = new Reflections(Images.class.getPackage().getName() + ".icon.map", new ResourcesScanner());
		final Collection<String> resourceNames = reflections.getResources(name -> name.toLowerCase(Locale.ROOT).endsWith(".png")).stream().map(name -> '/' + name).collect(Collectors.toSet());
		for (int size = desiredSizePixels; size > 0; size--) {
			for (final String resourceName : resourceNames) {
				if (resourceName.toLowerCase(Locale.ROOT).contains("x" + size + ".")) {
					try (final InputStream stream = Images.class.getResourceAsStream(resourceName)) {
						final ImageData[] data = new ImageLoader().load(stream);
						if (data != null && data.length == 1) {
							log.log(Level.FINE, "Found {0}x{0} map icon for desired size {1} pixels.", new Integer[] { size, desiredSizePixels });
							return new Image(Display.getCurrent(), data[0]);
						}
					}
					catch (final IOException e) {
						throw new UncheckedIOException(e);
					}
				}
			}
		}
		throw new IllegalStateException("No map icon found for desired size " + desiredSizePixels + " pixels");
	}

}
