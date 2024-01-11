package io.github.albertus82.eqbulletin.gui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.eqbulletin.model.MapImage;
import io.github.albertus82.eqbulletin.resources.Messages;
import io.github.albertus82.jface.EnhancedErrorDialog;
import io.github.albertus82.jface.ImageUtils;
import io.github.albertus82.jface.Multilanguage;
import io.github.albertus82.jface.closeable.CloseableResource;
import io.github.albertus82.jface.i18n.LocalizedWidgets;
import io.github.albertus82.jface.preference.IPreferencesConfiguration;
import io.github.albertus82.util.ISupplier;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MapCanvas implements Multilanguage {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final boolean MAP_RESIZE_HQ = true;
		public static final short MAP_ZOOM_LEVEL = AUTO_SCALE;
	}

	private static final int AUTO_SCALE = 0;
	private static final int MAX_HQ_RESIZE_RATIO = 250;

	private final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private final Canvas canvas;

	@Getter
	private Earthquake earthquake;
	private MapImage mapImage;

	private Image image;

	private int zoomLevel = configuration.getShort(Preference.MAP_ZOOM_LEVEL, Defaults.MAP_ZOOM_LEVEL);

	private Image resized;

	private volatile boolean dirty = false;

	private final LocalizedWidgets localizedWidgets = new LocalizedWidgets();

	private static MapCanvas instance;

	MapCanvas(@NonNull final Composite parent, final Object layoutData) {
		canvas = new Canvas(parent, SWT.BORDER);
		if (layoutData != null) {
			canvas.setLayoutData(layoutData);
		}
		canvas.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE)); // match background color of GFZ maps
		canvas.addPaintListener(e -> paint(e.gc));

		final Menu contextMenu = new Menu(canvas);

		final MenuItem zoomMenuItem = newLocalizedMenuItem(contextMenu, SWT.CASCADE, "label.menu.item.zoom");

		final Menu zoomSubMenu = new Menu(zoomMenuItem);
		zoomMenuItem.setMenu(zoomSubMenu);

		final Map<Integer, MenuItem> zoomSubMenuItems = new HashMap<>();
		for (final int level : getZoomLevels()) {
			final MenuItem item = newLocalizedMenuItem(zoomSubMenu, SWT.RADIO, () -> Messages.get("label.menu.item.zoom." + (level == AUTO_SCALE ? "auto" : "custom"), level));
			zoomSubMenuItems.put(level, item);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					setZoomLevel(level);
				}
			});
			if (level == AUTO_SCALE) {
				new MenuItem(zoomSubMenu, SWT.SEPARATOR);
			}
		}

		zoomSubMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(final MenuEvent e) {
				for (final Entry<Integer, MenuItem> entry : zoomSubMenuItems.entrySet()) {
					entry.getValue().setSelection(entry.getKey().equals(zoomLevel));
				}
			}
		});

		new MenuItem(contextMenu, SWT.SEPARATOR);

		final MenuItem saveMenuItem = newLocalizedMenuItem(contextMenu, SWT.PUSH, "label.menu.item.save.map");
		saveMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				saveImage();
			}
		});
		canvas.setMenu(contextMenu);
		canvas.addMenuDetectListener(e -> saveMenuItem.setEnabled(canSaveImage()));

		configureMouseWheelListener();
		configureKeyListener();

		setInstance(this);
	}

	private void configureMouseWheelListener() {
		canvas.addMouseWheelListener(e -> {
			if (image != null && e.count != 0) {
				final float autoscaleRatio = getAutoscaleRatio();
				final int[] nearestValues = getZoomNearestValues(zoomLevel == AUTO_SCALE ? autoscaleRatio : zoomLevel);
				if (e.count > 0) { // Zoom in
					zoomIn(autoscaleRatio, nearestValues[1]);
				}
				else if (e.count < 0) { // Zoom out
					zoomOut(autoscaleRatio, nearestValues[0]);
				}
			}
		});
	}

	private void configureKeyListener() {
		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (image != null) {
					final float autoscaleRatio = getAutoscaleRatio();
					final int[] nearestValues = getZoomNearestValues(zoomLevel == AUTO_SCALE ? autoscaleRatio : zoomLevel);
					if (e.keyCode == '+' || e.keyCode == SWT.KEYPAD_ADD) { // Zoom in
						zoomIn(autoscaleRatio, nearestValues[1]);
					}
					else if (e.keyCode == '-' || e.keyCode == SWT.KEYPAD_SUBTRACT) { // Zoom out
						zoomOut(autoscaleRatio, nearestValues[0]);
					}
				}
			}
		});
	}

	private void zoomIn(final float autoscaleRatio, final int higherValue) {
		if (zoomLevel != AUTO_SCALE && zoomLevel < autoscaleRatio && autoscaleRatio < higherValue) {
			setZoomLevel(AUTO_SCALE);
		}
		else {
			setZoomLevel(higherValue);
		}
	}

	private void zoomOut(final float autoscaleRatio, final int lowerValue) {
		if (zoomLevel != AUTO_SCALE && zoomLevel > autoscaleRatio && autoscaleRatio > lowerValue) {
			setZoomLevel(AUTO_SCALE);
		}
		else {
			setZoomLevel(lowerValue);
		}
	}

	private static void setInstance(final MapCanvas instance) {
		MapCanvas.instance = instance;
	}

	public void setZoomLevel(final int zoomLevel) {
		if (this.zoomLevel != zoomLevel) {
			dirty = this.zoomLevel > zoomLevel || this.zoomLevel == AUTO_SCALE;
			this.zoomLevel = zoomLevel;
			refresh();
		}
	}

	public void refresh() {
		if (Util.isCocoa()) {
			canvas.redraw(); // slower but fixes empty canvas bug on macOS 11
		}
		else { // faster
			try (final CloseableResource<GC> cr = new CloseableResource<>(new GC(canvas))) {
				paint(cr.getResource());
			}
		}
	}

	public void clear() {
		if (image != null) {
			image.dispose();
			image = null;
		}
		earthquake = null;
		dirty = true;
		refresh();
	}

	private void paint(@NonNull final GC gc) {
		if (image == null) {
			cleanDirt(gc);
		}
		else {
			final Rectangle originalRect = image.getBounds();
			final Rectangle resizedRect = getResizedRectangle(zoomLevel);

			if (resizedRect.height == originalRect.height) { // Do not resize!
				cleanDirt(gc);
				gc.drawImage(image, resizedRect.x, resizedRect.y);
			}
			else {
				if (configuration.getBoolean(Preference.MAP_RESIZE_HQ, Defaults.MAP_RESIZE_HQ) && (zoomLevel == AUTO_SCALE || zoomLevel % 100 != 0 && zoomLevel <= MAX_HQ_RESIZE_RATIO)) {
					log.debug("HQ resizing scale {}.", zoomLevel);
					final Image oldImage = resized;
					resized = ImageUtils.resize(image, resizedRect.height / (float) originalRect.height);
					cleanDirt(gc);
					gc.drawImage(resized, resizedRect.x, resizedRect.y);
					if (oldImage != null && oldImage != resized) {
						oldImage.dispose();
					}
				}
				else { // Fast low-quality resizing
					log.debug("LQ Resizing scale {}.", zoomLevel);
					cleanDirt(gc);
					gc.drawImage(image, 0, 0, originalRect.width, originalRect.height, resizedRect.x, resizedRect.y, resizedRect.width, resizedRect.height);
				}
			}
		}
	}

	private void cleanDirt(@NonNull final GC gc) {
		if (dirty) {
			final Rectangle canvasBounds = canvas.getBounds();
			gc.fillRectangle(0, 0, canvasBounds.width, canvasBounds.height);
			dirty = false;
		}
	}

	static int[] getZoomNearestValues(float value) {
		final int[] zoomLevels = getZoomLevels();
		value = Math.max(zoomLevels[1], Math.min(value, zoomLevels[zoomLevels.length - 1])); // limit input range
		final int[] values = new int[] { zoomLevels[1], zoomLevels[zoomLevels.length - 1] };
		for (final int zoomLevel : zoomLevels) {
			if (zoomLevel != AUTO_SCALE) {
				if (zoomLevel < value) {
					values[0] = zoomLevel;
				}
				else if (zoomLevel > value) {
					values[1] = zoomLevel;
					break;
				}
			}
		}
		return values;
	}

	private float getAutoscaleRatio() {
		final Rectangle originalRect = image.getBounds();
		final Rectangle resizedRect = getResizedRectangle(AUTO_SCALE);

		if (originalRect.width > originalRect.height) {
			return 100f * resizedRect.width / originalRect.width;
		}
		else {
			return 100f * resizedRect.height / originalRect.height;
		}
	}

	private Rectangle getResizedRectangle(final int scalePercent) {
		final Rectangle imageSize = image.getBounds();
		final Rectangle canvasSize = canvas.getClientArea();

		final int width;
		final int height;

		if (scalePercent == AUTO_SCALE) {
			// Autoscale
			final float imageRatio = (float) imageSize.width / imageSize.height;
			final float canvasRatio = (float) canvasSize.width / canvasSize.height;
			if (canvasRatio > imageRatio) {
				width = Math.round((float) imageSize.width * canvasSize.height / imageSize.height);
				height = canvasSize.height;
			}
			else {
				width = canvasSize.width;
				height = Math.round((float) imageSize.height * canvasSize.width / imageSize.width);
			}
		}
		else {
			width = Math.round(imageSize.width * scalePercent / 100f);
			height = Math.round(imageSize.height * scalePercent / 100f);
		}
		final int x = Math.round((canvasSize.width - width) / 2f);
		final int y = Math.round((canvasSize.height - height) / 2f);

		return new Rectangle(x, y, width, height);
	}

	private boolean canSaveImage() {
		return image != null && earthquake != null;
	}

	private void saveImage() {
		if (canSaveImage()) {
			final FileDialog saveDialog = new FileDialog(canvas.getShell(), SWT.SAVE);
			saveDialog.setFilterExtensions(new String[] { "*.JPG;*.jpg" });
			saveDialog.setFileName(earthquake.getGuid().toLowerCase() + ".jpg");
			saveDialog.setOverwrite(true);
			final String fileName = saveDialog.open();
			if (fileName != null && !fileName.trim().isEmpty()) {
				try {
					Files.write(Paths.get(fileName), mapImage.getBytes());
				}
				catch (final Exception e) {
					final String message = Messages.get("error.image.save", fileName);
					log.warn("Cannot save map image file \"" + fileName + "\":", e);
					EnhancedErrorDialog.openError(canvas.getShell(), EarthquakeBulletinGui.getApplicationName(), message, IStatus.WARNING, e, Images.getAppIconArray());
				}
			}
		}
	}

	@Override
	public void updateLanguage() {
		localizedWidgets.resetAllTexts();
	}

	private MenuItem newLocalizedMenuItem(@NonNull final Menu parent, final int style, @NonNull final String messageKey) {
		return newLocalizedMenuItem(parent, style, () -> Messages.get(messageKey));
	}

	private MenuItem newLocalizedMenuItem(@NonNull final Menu parent, final int style, @NonNull final ISupplier<String> textSupplier) {
		return localizedWidgets.putAndReturn(new MenuItem(parent, style), textSupplier).getKey();
	}

	public static int[] getZoomLevels() {
		final int[] a = new int[] { AUTO_SCALE, 10, 12, 15, 20, 25, 30, 40, 50, 60, 80, 100, 120, 150, 200, 250, 300, 400, 500 };
		Arrays.sort(a);
		return a;
	}

	public static synchronized void setMapImage(final MapImage mapImage, final Earthquake earthquake) {
		if (instance != null) {
			log.debug("Setting map image canvas for {}...", earthquake);
			update(mapImage, earthquake);
		}
	}

	public static synchronized void updateMapImage(final MapImage mapImage, final Earthquake earthquake) {
		if (instance != null && instance.earthquake != null && earthquake.getGuid().equals(instance.earthquake.getGuid())) { // Only if the provided image belongs to the current earthquake.
			log.debug("Updating map image canvas for {}...", earthquake);
			update(mapImage, earthquake);
		}
	}

	private static void update(final MapImage mapImage, final Earthquake earthquake) {
		if (mapImage.equals(instance.mapImage)) {
			log.debug("Map image canvas already set for {}.", earthquake);
		}
		else {
			final byte[] imageBytes = mapImage.getBytes();
			if (imageBytes != null && imageBytes.length > 0) {
				try (final InputStream is = new ByteArrayInputStream(imageBytes)) {
					final Image oldImage = instance.image;
					instance.image = new Image(instance.canvas.getDisplay(), is);
					instance.mapImage = mapImage;
					instance.earthquake = earthquake;
					instance.dirty = true;
					instance.refresh();
					if (oldImage != null) {
						oldImage.dispose();
					}
					log.debug("Map image canvas set/updated for {}.", earthquake);
				}
				catch (final Exception e) {
					log.warn("Cannot update map image canvas for " + earthquake + ':', e);
				}
			}
		}
	}

}
