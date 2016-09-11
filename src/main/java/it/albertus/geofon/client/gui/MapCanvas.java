package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.job.DownloadMapJob;
import it.albertus.geofon.client.gui.listener.MapCanvasContextMenuListener;
import it.albertus.geofon.client.gui.listener.MapCanvasPaintListener;
import it.albertus.geofon.client.gui.listener.SaveMapSelectionListener;
import it.albertus.geofon.client.resources.Messages;

import java.io.ByteArrayInputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class MapCanvas {

	private final MapCache cache = new MapCache();
	private final Canvas canvas;

	private Image image;
	private String guid;

	private DownloadMapJob downloadMapJob;

	private final Menu contextMenu;
	private final MenuItem downloadMenuItem;

	public MapCanvas(final Composite parent) {
		canvas = new Canvas(parent, SWT.BORDER);
		canvas.setBackground(getBackgroundColor());
		canvas.addPaintListener(new MapCanvasPaintListener(this));

		contextMenu = new Menu(canvas);
		downloadMenuItem = new MenuItem(contextMenu, SWT.PUSH);
		downloadMenuItem.setText(Messages.get("lbl.menu.item.save.map"));
		downloadMenuItem.addSelectionListener(new SaveMapSelectionListener(this));
		canvas.setMenu(contextMenu);
		canvas.addMenuDetectListener(new MapCanvasContextMenuListener(this));
	}

	public Image getImage() {
		return image;
	}

	public String getGuid() {
		return guid;
	}

	public void setImage(final String guid, final byte[] imageBytes) {
		if (imageBytes != null && imageBytes.length > 0) {
			cache.put(guid, imageBytes);
			try (final ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
				final Image oldImage = this.image;
				this.image = new Image(canvas.getDisplay(), bais);
				this.guid = guid;
				canvas.notifyListeners(SWT.Paint, new Event());
				if (oldImage != null) {
					oldImage.dispose();
				}
			}
			catch (final Exception e) {/* Ignore */}
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

	public void updateTexts() {
		downloadMenuItem.setText(Messages.get("lbl.menu.item.save.map"));
	}

	protected Color getBackgroundColor() {
		return canvas.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public MapCache getCache() {
		return cache;
	}

	public Menu getContextMenu() {
		return contextMenu;
	}

	public MenuItem getDownloadMenuItem() {
		return downloadMenuItem;
	}

	public DownloadMapJob getDownloadMapJob() {
		return downloadMapJob;
	}

	public void setDownloadMapJob(DownloadMapJob downloadMapJob) {
		this.downloadMapJob = downloadMapJob;
	}

}
