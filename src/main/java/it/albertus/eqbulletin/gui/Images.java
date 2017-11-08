package it.albertus.eqbulletin.gui;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

import it.albertus.util.logging.LoggerFactory;

public class Images {

	private static final Logger logger = LoggerFactory.getLogger(Images.class);

	// Main application icon (in various formats)
	private static final Collection<Image> mainIcons = new LinkedHashSet<>();

	private Images() {
		throw new IllegalAccessError();
	}

	static {
		try (final InputStream stream = Images.class.getResourceAsStream("main.ico")) {
			for (final ImageData data : new ImageLoader().load(stream)) {
				mainIcons.add(new Image(Display.getCurrent(), data));
			}
		}
		catch (final IOException e) {
			logger.log(Level.WARNING, e.toString(), e);
		}
	}

	public static Image[] getMainIcons() {
		return mainIcons.toArray(new Image[mainIcons.size()]);
	}

}
