package it.albertus.eqbulletin.gui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.job.DownloadMapJob;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.MapCache;
import it.albertus.jface.EnhancedErrorDialog;
import it.albertus.jface.HqImageResizer;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.logging.LoggerFactory;

public class MapCanvas {

	public static class Defaults {
		public static final boolean MAP_RESIZE_HQ = true;
		public static final short MAP_ZOOM_LEVEL = 0;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final int AUTO_SCALE = 0;

	private static final Logger logger = LoggerFactory.getLogger(MapCanvas.class);

	private static final LinkedList<Integer> zoomLevels = new LinkedList<>(new TreeSet<>(Arrays.asList(AUTO_SCALE, 10, 15, 20, 25, 30, 40, 50, 60, 80, 100, 120, 150, 200, 250, 300, 400, 500)));

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private final MapCache cache = new MapCache();
	private final Canvas canvas;

	private String guid;
	private Image image;

	private int zoomLevel = configuration.getShort(Preference.MAP_ZOOM_LEVEL, Defaults.MAP_ZOOM_LEVEL);

	private Image resized;

	private DownloadMapJob downloadMapJob;

	private final MenuItem downloadMenuItem;
	private final MenuItem zoomMenuItem;
	private final Map<Integer, MenuItem> zoomSubMenuItems = new HashMap<>();

	public MapCanvas(final Composite parent) {
		canvas = new Canvas(parent, SWT.BORDER);
		canvas.setBackground(getBackgroundColor());
		canvas.addPaintListener(e -> paintImage(zoomLevel));

		final Menu contextMenu = new Menu(canvas);

		zoomMenuItem = new MenuItem(contextMenu, SWT.CASCADE);
		zoomMenuItem.setData("lbl.menu.item.zoom");
		zoomMenuItem.setText(Messages.get(zoomMenuItem.getData().toString()));

		final Menu zoomSubMenu = new Menu(zoomMenuItem);
		zoomMenuItem.setMenu(zoomSubMenu);

		for (final int level : zoomLevels) {
			final MenuItem item = new MenuItem(zoomSubMenu, SWT.RADIO);
			zoomSubMenuItems.put(level, item);
			item.setData("lbl.menu.item.zoom." + (level == 0 ? "auto" : "custom"));
			item.setText(Messages.get(item.getData().toString(), level));
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					setZoomLevel(level);
				}
			});
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

		downloadMenuItem = new MenuItem(contextMenu, SWT.PUSH);
		downloadMenuItem.setData("lbl.menu.item.save.map");
		downloadMenuItem.setText(Messages.get(downloadMenuItem.getData().toString()));
		downloadMenuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				saveImage();
			}
		});
		canvas.setMenu(contextMenu);
		canvas.addMenuDetectListener(e -> downloadMenuItem.setEnabled(canSaveImage()));

		canvas.addMouseWheelListener(e -> {
			if (image != null && e.count != 0) {
				final int[] nearestValues = getZoomNearestValues(zoomLevel == 0 ? getAutoscaleRatio() : zoomLevel);
				if (e.count > 0) { // Zoom in
					setZoomLevel(nearestValues[1]);
				}
				else if (e.count < 0) { // Zoom out
					setZoomLevel(nearestValues[0]);
				}
			}
		});

		canvas.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if (image != null) {
					final int[] nearestValues = getZoomNearestValues(zoomLevel == 0 ? getAutoscaleRatio() : zoomLevel);
					if (e.keyCode == '+' || e.keyCode == SWT.KEYPAD_ADD) { // Zoom in
						setZoomLevel(nearestValues[1]);
					}
					else if (e.keyCode == '-' || e.keyCode == SWT.KEYPAD_SUBTRACT) { // Zoom out
						setZoomLevel(nearestValues[0]);
					}
				}
			}
		});
	}

	public void setZoomLevel(final int zoomLevel) {
		if (this.zoomLevel != zoomLevel) {
			paintImage(zoomLevel);
			this.zoomLevel = zoomLevel;
		}
	}

	public void refresh() {
		paintImage(zoomLevel);
	}

	public Image getImage() {
		return image;
	}

	public String getGuid() {
		return guid;
	}

	public void setImage(final String guid, final MapImage mapImage) {
		final byte[] imageBytes = mapImage.getBytes();
		if (imageBytes != null && imageBytes.length > 0) {
			cache.put(guid, mapImage);
			try (final InputStream is = new ByteArrayInputStream(imageBytes)) {
				final Image oldImage = this.image;
				this.image = new Image(canvas.getDisplay(), is);
				this.guid = guid;
				paintImage(this.zoomLevel);
				if (oldImage != null) {
					oldImage.dispose();
				}
			}
			catch (final Exception e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
	}

	public void clear() {
		final GC gc = new GC(canvas);
		gc.setBackground(getBackgroundColor());
		final Rectangle canvasBounds = canvas.getBounds();
		gc.fillRectangle(0, 0, canvasBounds.width, canvasBounds.height);
		gc.dispose();
		if (image != null) {
			image.dispose();
			image = null;
		}
		guid = null;
	}

	private void paintImage(final int scalePercent) {
		if (image == null) {
			return;
		}
		final Rectangle originalRect = image.getBounds();
		final Rectangle resizedRect = getResizedRectangle(scalePercent);

		final GC gc = new GC(canvas);
		if (resizedRect.height == originalRect.height) { // Do not resize!
			prepareCanvas(gc, scalePercent);
			gc.drawImage(image, resizedRect.x, resizedRect.y);
		}
		else {
			if (configuration.getBoolean(Preference.MAP_RESIZE_HQ, Defaults.MAP_RESIZE_HQ) && (scalePercent == 0 || scalePercent % 100 != 0 && scalePercent < 300)) {
				logger.log(Level.FINE, "Resizing HQ scale={0}", scalePercent);
				final Image oldImage = resized;
				resized = HqImageResizer.resize(image, resizedRect.height / (float) originalRect.height);
				prepareCanvas(gc, scalePercent);
				gc.drawImage(resized, resizedRect.x, resizedRect.y);
				if (oldImage != null && oldImage != resized) {
					oldImage.dispose();
				}
			}
			else { // Fast low-quality resizing
				logger.log(Level.FINE, "Resizing LQ scale={0}", scalePercent);
				prepareCanvas(gc, scalePercent);
				gc.drawImage(image, 0, 0, originalRect.width, originalRect.height, resizedRect.x, resizedRect.y, resizedRect.width, resizedRect.height);
			}
		}
		gc.dispose();
	}

	private void prepareCanvas(final GC gc, final int scalePercent) {
		if (zoomLevel > scalePercent || zoomLevel == 0) { // Zoom out/Auto scale
			gc.setBackground(getBackgroundColor());
			final Rectangle canvasBounds = canvas.getBounds();
			gc.fillRectangle(0, 0, canvasBounds.width, canvasBounds.height);
		}
	}

	static int[] getZoomNearestValues(float value) {
		value = Math.max(zoomLevels.get(1), Math.min(value, zoomLevels.getLast())); // limit input range
		final int[] values = new int[] { zoomLevels.get(1), zoomLevels.getLast() };
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

	private Rectangle getResizedRectangle(final Number scalePercent) {
		final Rectangle imageSize = image.getBounds();
		final Rectangle canvasSize = canvas.getBounds();

		final int width;
		final int height;

		if (scalePercent == null || scalePercent.floatValue() == 0) {
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
			width = Math.round(imageSize.width * scalePercent.floatValue() / 100);
			height = Math.round(imageSize.height * scalePercent.floatValue() / 100);
		}
		final int x = Math.round((canvasSize.width - width) / 2f);
		final int y = Math.round((canvasSize.height - height) / 2f);

		return new Rectangle(x, y, width, height);
	}

	private boolean canSaveImage() {
		return image != null && guid != null;
	}

	private void saveImage() {
		if (canSaveImage()) {
			final FileDialog saveDialog = new FileDialog(canvas.getShell(), SWT.SAVE);
			saveDialog.setFilterExtensions(new String[] { "*.JPG;*.jpg" });
			saveDialog.setFileName(guid.toLowerCase() + ".jpg");
			saveDialog.setOverwrite(true);
			final String fileName = saveDialog.open();
			if (fileName != null && !fileName.trim().isEmpty()) {
				try {
					Files.write(Paths.get(fileName), cache.get(guid).getBytes());
				}
				catch (final Exception e) {
					final String message = Messages.get("err.image.save", fileName);
					logger.log(Level.WARNING, message, e);
					EnhancedErrorDialog.openError(canvas.getShell(), Messages.get("lbl.window.title"), message, IStatus.WARNING, e, Images.getMainIcons());
				}
			}
		}
	}

	public void updateTexts() {
		downloadMenuItem.setText(Messages.get(downloadMenuItem.getData().toString()));
		zoomMenuItem.setText(Messages.get(zoomMenuItem.getData().toString()));
		for (final Entry<Integer, MenuItem> entry : zoomSubMenuItems.entrySet()) {
			entry.getValue().setText(Messages.get(entry.getValue().getData().toString(), entry.getKey()));
		}
	}

	private Color getBackgroundColor() {
		return canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public MapCache getCache() {
		return cache;
	}

	public DownloadMapJob getDownloadMapJob() {
		return downloadMapJob;
	}

	public void setDownloadMapJob(DownloadMapJob downloadMapJob) {
		this.downloadMapJob = downloadMapJob;
	}

	public static Collection<Integer> getZoomLevels() {
		return Collections.unmodifiableCollection(zoomLevels);
	}

}
