package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.MapCanvas;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MapCanvasListener implements Listener {

	private final MapCanvas mapCanvas;

	public MapCanvasListener(final MapCanvas mapCanvas) {
		this.mapCanvas = mapCanvas;
	}

	@Override
	public void handleEvent(final Event event) {
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

			int top = (int) ((canvasSize.height - newHeight) / 2.0);
			int left = (int) ((canvasSize.width - newWidth) / 2.0);

			final GC gc = new GC(canvas);
			gc.drawImage(image, 0, 0, imageSize.width, imageSize.height, left, top, newWidth, newHeight);
		}
	}

}
