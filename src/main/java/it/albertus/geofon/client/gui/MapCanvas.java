package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.listener.MapCanvasListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class MapCanvas {

	private final MapCache cache = new MapCache();

	private Canvas canvas;
	private Image image;

	public MapCanvas(final Composite parent) {
		canvas = new Canvas(parent, SWT.NULL);
		canvas.addPaintListener(new MapCanvasListener(this));
	}

	public Image getImage() {
		return image;
	}

	public void setImage(final Image image) {
		this.image = image;
		canvas.notifyListeners(SWT.Paint, new Event());
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public MapCache getCache() {
		return cache;
	}

}
