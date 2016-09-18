package it.albertus.earthquake.gui.map;

import it.albertus.earthquake.resources.Messages;
import it.albertus.util.NewLine;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MapDialog extends Dialog {

	public static final MapType DEFAULT_TYPE = MapType.TERRAIN;
	public static final int DEFAULT_ZOOM = 1;

	protected static final String HTML_FILE_NAME = "map.html";
	protected static final int BUTTON_WIDTH = 90;

	private float centerLat;
	private float centerLng;
	private int zoom = DEFAULT_ZOOM;
	private MapType type = DEFAULT_TYPE;
	private final Set<Marker> markers = new HashSet<>();

	private volatile int returnCode = SWT.CANCEL;

	private Image[] images;

	public MapDialog(final Shell parent) {
		this(parent, SWT.SHEET | SWT.RESIZE | SWT.MAX);
	}

	public MapDialog(final Shell parent, final int style) {
		super(parent, style);
	}

	public Composite createButtonBox(final Shell shell, final Browser browser) {
		final Composite buttonComposite = new Composite(shell, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(buttonComposite);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(buttonComposite);

		final Button closeButton = new Button(buttonComposite, SWT.PUSH);
		closeButton.setText(Messages.get("lbl.button.close"));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.FILL).grab(true, false).minSize(BUTTON_WIDTH, SWT.DEFAULT).applyTo(closeButton);
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent se) {
				shell.close();
			}
		});

		shell.setDefaultButton(closeButton);
		return buttonComposite;
	}

	public int open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		final Image[] images = getImages();
		if (images != null && images.length > 0) {
			shell.setImages(images);
		}
		createContents(shell);
		final Point minimumSize = computeMinimumSize(shell);
		shell.setSize(computeSize(shell));
		shell.setMinimumSize(minimumSize);
		shell.open();
		final Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return returnCode;
	}

	protected Point computeSize(final Shell shell) {
		final Point normalShellSize = shell.getSize();
		int size = (int) (Math.min(normalShellSize.x, normalShellSize.y) / 1.25);
		return new Point(size, size);
	}

	protected Point computeMinimumSize(final Shell shell) {
		return shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
	}

	protected void createContents(final Shell shell) {
		GridLayoutFactory.swtDefaults().applyTo(shell);
		final Browser browser = createBrowser(shell);
		createButtonBox(shell, browser);
	}

	protected Browser createBrowser(final Shell shell) {
		final Browser browser = new Browser(shell, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);
		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent ce) {
				try {
					final Point browserSize = browser.getSize();
					browser.execute("document.getElementById('map_canvas').style.width= " + (browserSize.x - 20) + ";");
					browser.execute("document.getElementById('map_canvas').style.height= " + (browserSize.y - 20) + ";");
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			}
		});

		final URL pageUrl = getMapPage(shell);
		browser.setUrl(pageUrl != null ? pageUrl.toString() : "");
		return browser;
	}

	protected URL getMapPage(final Shell shell) {
		URL pageUrl = null;
		File tempFile = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(HTML_FILE_NAME)));
			tempFile = File.createTempFile("map", null);
			writer = new BufferedWriter(new FileWriter(tempFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("center: new google.maps.LatLng(0, 0)")) {
					line = line.replace("0, 0", centerLat + ", " + centerLng);
				}
				else if (line.contains("zoom: 1")) {
					line = line.replace("1", Integer.toString(zoom));
				}
				else if (line.contains("mapTypeId: google.maps.MapTypeId.TERRAIN")) {
					line = line.replace("TERRAIN", type.name());
				}
				else if (!markers.isEmpty() && line.contains("/* Markers */")) {
					int i = 1;
					final StringBuilder markersBlock = new StringBuilder();
					for (final Marker marker : markers) {
						markersBlock.append("var marker").append(i).append(" = new google.maps.Marker({").append(NewLine.SYSTEM_LINE_SEPARATOR);
						markersBlock.append('\t').append("position: new google.maps.LatLng(").append(marker.getLatitude()).append(", ").append(marker.getLongitude()).append("),").append(NewLine.SYSTEM_LINE_SEPARATOR);
						markersBlock.append('\t').append("map: map,").append(NewLine.SYSTEM_LINE_SEPARATOR);
						markersBlock.append('\t').append("title: '").append(marker.getTitle().replace("'", "\\'")).append("'").append(NewLine.SYSTEM_LINE_SEPARATOR);
						markersBlock.append("});").append(NewLine.SYSTEM_LINE_SEPARATOR);
						i++;
					}
					line = line.replace("/* Markers */", markersBlock.toString().trim());
				}
				writer.write(line);
				writer.newLine();
			}
			pageUrl = tempFile.toURI().toURL();
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			}
			catch (final Exception e) {/* Ignore */}
			try {
				reader.close();
			}
			catch (final Exception e) {/* Ignore */}
		}

		if (tempFile != null) {
			final File fileToDelete = tempFile;
			shell.addListener(SWT.Close, new Listener() {
				@Override
				public void handleEvent(final Event event) {
					try {
						fileToDelete.delete();
					}
					catch (final Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		return pageUrl;
	}

	public float getCenterLat() {
		return centerLat;
	}

	public void setCenterLat(final float centerLat) {
		this.centerLat = centerLat;
	}

	public float getCenterLng() {
		return centerLng;
	}

	public void setCenterLng(final float centerLng) {
		this.centerLng = centerLng;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(final int zoom) {
		this.zoom = zoom;
	}

	public int getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(final int returnCode) {
		this.returnCode = returnCode;
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(final Image[] images) {
		this.images = images;
	}

	public MapType getType() {
		return type;
	}

	public void setType(final MapType type) {
		this.type = type;
	}

	public Set<Marker> getMarkers() {
		return markers;
	}

}
