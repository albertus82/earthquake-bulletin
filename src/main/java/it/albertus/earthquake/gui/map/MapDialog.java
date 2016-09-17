package it.albertus.earthquake.gui.map;

import it.albertus.earthquake.resources.Messages;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class MapDialog extends Dialog {

	public static final int DEFAULT_ZOOM = 1;

	private static double centerLat;
	private static double centerLng;
	private static int zoom = DEFAULT_ZOOM;

	private Double northEastLat;
	private Double southWestLat;
	private Double northEastLng;
	private Double southWestLng;

	private volatile int returnCode = SWT.CANCEL;

	private Image[] images;

	public MapDialog(final Shell shell) {
		super(shell, SWT.SHEET | SWT.RESIZE | SWT.WRAP | SWT.MAX);
	}

	public int open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		final Image[] images = getImages();
		if (images != null && images.length > 0) {
			shell.setImages(images);
		}
		createContents(shell);
		shell.open();
		final Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return returnCode;
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(1, false));

		final Browser browser = new Browser(shell, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);
		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent ce) {
				try {
					browser.execute("document.getElementById('map_canvas').style.width= " + (browser.getSize().x - 20) + ";");
					browser.execute("document.getElementById('map_canvas').style.height= " + (browser.getSize().y - 20) + ";");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		File tempFile = null;
		String pageFileName;
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(new File(getClass().getResource("map.html").toURI())));
			tempFile = File.createTempFile("map", null);
			bw = new BufferedWriter(new FileWriter(tempFile));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("center : new google.maps.LatLng(0, 0)")) {
					line = line.replace("0, 0", centerLat + ", " + centerLng);
				}
				else if (line.contains("zoom : 1")) {
					line = line.replace("1", Integer.toString(zoom));
				}
				bw.write(line);
				bw.newLine();
			}
			pageFileName = tempFile.getPath();
		}
		catch (final Exception e) {
			e.printStackTrace();
			pageFileName = getClass().getResource("map.html").toString();
		}
		finally {
			try {
				bw.close();
			}
			catch (final Exception e) {/* Ignore */}
			try {
				br.close();
			}
			catch (final Exception e) {/* Ignore */}
		}
		browser.setUrl(pageFileName);

		final Composite buttonComposite = new Composite(shell, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(buttonComposite);

		final Button okButton = new Button(buttonComposite, SWT.PUSH);
		okButton.setText(Messages.get("lbl.button.ok"));
		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gridData.minimumWidth = 90;
		okButton.setLayoutData(gridData);
		okButton.setFocus();
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent se) {
				try {
					centerLat = (double) browser.evaluate("return map.getCenter().lat();");
					centerLng = (double) browser.evaluate("return map.getCenter().lng();");
					zoom = ((Number) browser.evaluate("return map.getZoom();")).intValue();
					northEastLat = (Double) browser.evaluate("return map.getBounds().getNorthEast().lat();");
					southWestLat = (Double) browser.evaluate("return map.getBounds().getSouthWest().lat();");
					northEastLng = (Double) browser.evaluate("return map.getBounds().getNorthEast().lng();");
					southWestLng = (Double) browser.evaluate("return map.getBounds().getSouthWest().lng();");
					returnCode = SWT.OK;
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
				finally {
					shell.close();
				}
			}
		});

		final Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText(Messages.get("lbl.button.cancel"));
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gridData.minimumWidth = 90;
		cancelButton.setLayoutData(gridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent se) {
				shell.close();
			}
		});
		shell.setDefaultButton(okButton);

		// Delete temp file immediately
		if (tempFile != null) {
			final File fileToDelete = tempFile;
			shell.addListener(SWT.Close, new Listener() {
				@Override
				public void handleEvent(Event event) {
					try {
						fileToDelete.delete();
					}
					catch (final Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public Double getNorthEastLat() {
		return northEastLat;
	}

	public Double getSouthWestLat() {
		return southWestLat;
	}

	public Double getNorthEastLng() {
		return northEastLng;
	}

	public Double getSouthWestLng() {
		return southWestLng;
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	public static double getCenterLat() {
		return centerLat;
	}

	public static double getCenterLng() {
		return centerLng;
	}

	public static int getZoom() {
		return zoom;
	}

}
