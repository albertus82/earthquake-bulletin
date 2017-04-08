package it.albertus.earthquake.gui.listener;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.MapCanvas;
import it.albertus.jface.HqImageResizer;

public class MapCanvasPaintListener implements PaintListener {

	public static class Defaults {
		public static final boolean MAP_RESIZE_HQ = true;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private final MapCanvas mapCanvas;
	private Image resized;

	public MapCanvasPaintListener(final MapCanvas mapCanvas) {
		this.mapCanvas = mapCanvas;
	}

	@Override
	public void paintControl(final PaintEvent pe) {
		final Image image = mapCanvas.getImage();
		if (image != null) {
			final Canvas canvas = mapCanvas.getCanvas();
			final Rectangle imageSize = image.getBounds();
			final double imageRatio = 1.0 * imageSize.width / imageSize.height;
			final Rectangle canvasSize = canvas.getBounds();
			final double canvasRatio = 1.0 * canvasSize.width / canvasSize.height;

			int newHeight;
			int newWidth;

			if (canvasRatio > imageRatio) {
				newWidth = (int) (imageSize.width * (1.0 * canvasSize.height / imageSize.height));
				newHeight = canvasSize.height;
			}
			else {
				newWidth = canvasSize.width;
				newHeight = (int) (imageSize.height * (1.0 * canvasSize.width / imageSize.width));
			}

			// Allow reduction only
			if (newWidth > imageSize.width || newHeight > imageSize.height) {
				newWidth = imageSize.width;
				newHeight = imageSize.height;
			}

			final int top = (int) ((canvasSize.height - newHeight) / 2.0);
			final int left = (int) ((canvasSize.width - newWidth) / 2.0);

			final GC gc = new GC(canvas);

			if (newHeight == imageSize.height) { // Do not resize!
				gc.drawImage(image, left, top);
			}
			else {
				if (EarthquakeBulletinConfiguration.getInstance().getBoolean("map.resize.hq", Defaults.MAP_RESIZE_HQ)) {
					final Image oldImage = resized;
					final float scale = newHeight / (float) imageSize.height;
					resized = HqImageResizer.resize(image, scale);
					gc.drawImage(resized, left, top);
					if (oldImage != null && oldImage != resized) {
						oldImage.dispose();
					}
				}
				else { // Fast low-quality resizing
					gc.drawImage(image, 0, 0, imageSize.width, imageSize.height, left, top, newWidth, newHeight);
				}
			}
			gc.dispose();
		}
	}

}
