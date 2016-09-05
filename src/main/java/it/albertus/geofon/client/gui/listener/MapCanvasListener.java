package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.MapCanvas;
import it.albertus.jface.HqImageResizer;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class MapCanvasListener implements PaintListener {

	private final MapCanvas mapCanvas;

	private Image resized;

	public MapCanvasListener(final MapCanvas mapCanvas) {
		this.mapCanvas = mapCanvas;
	}

	@Override
	public void paintControl(PaintEvent e) {
		final Image image = mapCanvas.getImage();
		if (image != null) {
			final Canvas canvas = mapCanvas.getCanvas();
			final Rectangle imageSize = image.getBounds();
			double imageRatio = 1.0 * imageSize.width / imageSize.height;

			final Rectangle canvasSize = canvas.getBounds();

			double canvasRatio = 1.0 * canvasSize.width / canvasSize.height;

			int newHeight;
			int newWidth;

			if (canvasRatio > imageRatio) {
				newWidth = (int) (imageSize.width * (1.0 * canvasSize.height / imageSize.height));
				newHeight = (int) (canvasSize.height);
			}
			else {
				newWidth = (int) (canvasSize.width);
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

			if (true) { // TODO configuration
				final Image oldImage = resized;
				resized = HqImageResizer.resize(image, newHeight / (float) imageSize.height);
				gc.drawImage(resized, left, top);
				if (oldImage != null) {
					oldImage.dispose();
				}
			}
			else {
				gc.drawImage(image, 0, 0, imageSize.width, imageSize.height, left, top, newWidth, newHeight);
			}
		}
	}

}
