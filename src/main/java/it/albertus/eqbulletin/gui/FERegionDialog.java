package it.albertus.eqbulletin.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import gov.usgs.cr.hazards.feregion.fe_1995.Coordinates;
import gov.usgs.cr.hazards.feregion.fe_1995.FERegion;
import gov.usgs.cr.hazards.feregion.fe_1995.Region;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.maps.MapDialog;
import it.albertus.jface.maps.MapMarker;
import it.albertus.jface.maps.leaflet.LeafletMapControl;
import it.albertus.jface.maps.leaflet.LeafletMapDialog;
import it.albertus.jface.maps.leaflet.LeafletMapOptions;
import it.albertus.net.httpserver.html.HtmlUtils;
import it.albertus.util.IOUtils;
import it.albertus.util.NewLine;
import it.albertus.util.logging.LoggerFactory;

public class FERegionDialog extends Dialog {

	private static final Logger logger = LoggerFactory.getLogger(FERegionDialog.class);

	private static final short LATITUDE_MIN_VALUE = 0;
	private static final short LATITUDE_MAX_VALUE = 90;
	private static final short LONGITUDE_MIN_VALUE = 0;
	private static final short LONGITUDE_MAX_VALUE = 180;

	private static final short DIGITS = 2;
	private static final short FACTOR = 100;

	private static final String DEGREE_SIGN = "\u00B0";

	private final FERegion feregion;

	private Coordinates coordinates;

	private Text regionNumberText;
	private Text regionNameText;
	private Browser browser;

	public FERegionDialog(final Shell parent, final Coordinates coordinates) {
		super(parent);
		this.coordinates = coordinates;
		try {
			feregion = new FERegion();
		}
		catch (final IOException e) {
			throw new IOError(e);
		}
		setText(Messages.get("lbl.feregion.dialog.title"));
	}

	public FERegionDialog(final Shell parent) {
		this(parent, null);
	}

	protected static final String HTML_FILE_NAME = "map.html";

	private final LeafletMapOptions options = new LeafletMapOptions();

	public LeafletMapOptions getOptions() {
		return options;
	}

	private final Set<MapMarker> markers = new HashSet<MapMarker>();

	public Set<MapMarker> getMarkers() {
		return markers;
	}

	protected URI getMapPage(final Control control, final InputStream is) {
		URI pageUrl = null;
		File tempFile = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			isr = new InputStreamReader(is, "UTF-8");
			br = new BufferedReader(isr);
			tempFile = File.createTempFile("map-", ".html");
			fw = new FileWriter(tempFile);
			bw = new BufferedWriter(fw);
			String line;
			while ((line = br.readLine()) != null) {
				line = parseLine(line);
				if (line != null) {
					bw.write(line);
					bw.newLine();
				}
			}
			pageUrl = tempFile.toURI();
		}
		catch (final Exception e) {
			logger.log(Level.SEVERE, JFaceMessages.get("err.map.open"), e);
		}
		finally {
			IOUtils.closeQuietly(bw, fw, br, isr);
		}

		if (tempFile != null) {
			final File fileToDelete = tempFile;
			control.addListener(SWT.Close, new Listener() {
				@Override
				public void handleEvent(final Event event) {
					try {
						if (!fileToDelete.delete()) {
							fileToDelete.deleteOnExit();
						}
					}
					catch (final RuntimeException re) {
						logger.log(Level.WARNING, JFaceMessages.get("err.delete.temp", fileToDelete), re);
					}
				}
			});
		}
		return pageUrl;
	}

	protected String parseLine(final String line) {
		// Options
		if (line.contains(MapDialog.OPTIONS_PLACEHOLDER)) {
			final StringBuilder optionsBlock = new StringBuilder();
			optionsBlock.append(String.format("map.setView([%s, %s], %d);", getOptions().getCenterLat(), getOptions().getCenterLng(), getOptions().getZoom()));
			if (!options.getControls().containsKey(LeafletMapControl.LAYERS)) {
				optionsBlock.append(NewLine.SYSTEM_LINE_SEPARATOR);
				optionsBlock.append(String.format("L.tileLayer('%s', { maxZoom: %d, attribution: '%s' }).addTo(map);", HtmlUtils.escapeEcmaScript("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"), 19, HtmlUtils.escapeEcmaScript("&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a>")));
			}
			for (final Entry<LeafletMapControl, String> control : options.getControls().entrySet()) {
				optionsBlock.append(NewLine.SYSTEM_LINE_SEPARATOR);
				optionsBlock.append(String.format("map.addControl(L.control.%s(%s));", control.getKey().getConstructor(), control.getValue() == null ? "" : control.getValue().trim()));
			}
			return optionsBlock.toString().trim();
		}
		// Markers
		else if (line.contains(MapDialog.MARKERS_PLACEHOLDER)) {
			if (getMarkers().isEmpty()) {
				return null;
			}
			else {
				final StringBuilder markersBlock = new StringBuilder();
				for (final MapMarker marker : getMarkers()) {
					markersBlock.append(String.format("L.marker([%s, %s]).addTo(map).bindPopup('%s');", marker.getLatitude(), marker.getLongitude(), marker.getTitle() == null ? "" : HtmlUtils.escapeEcmaScript(marker.getTitle().replace(NewLine.SYSTEM_LINE_SEPARATOR, "<br />").trim())));
					markersBlock.append(NewLine.SYSTEM_LINE_SEPARATOR);
				}
				return markersBlock.toString().trim();
			}
		}
		else {
			return line;
		}
	}

	public void open() {
		final Shell shell = new Shell(getParent(), SWT.CLOSE | SWT.RESIZE | SWT.MAX);

		shell.setText(getText());
		shell.setImages(Images.getMainIconArray());
		GridLayoutFactory.swtDefaults().applyTo(shell);

		final Composite dialogArea = new Composite(shell, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(dialogArea);
		GridLayoutFactory.swtDefaults().applyTo(dialogArea);

		final Group coordinatesGroup = new Group(dialogArea, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(coordinatesGroup);
		GridLayoutFactory.swtDefaults().numColumns(8).applyTo(coordinatesGroup);
		coordinatesGroup.setText(Messages.get("lbl.feregion.dialog.coordinates"));

		final Label latitudeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(latitudeLabel);
		latitudeLabel.setText(Messages.get("lbl.feregion.dialog.latitude"));
		final Spinner latitudeSpinner = new Spinner(coordinatesGroup, SWT.BORDER);
		latitudeSpinner.setDigits(DIGITS);
		latitudeSpinner.setMinimum(LATITUDE_MIN_VALUE * FACTOR);
		latitudeSpinner.setMaximum(LATITUDE_MAX_VALUE * FACTOR);
		latitudeSpinner.setIncrement(FACTOR);
		latitudeSpinner.setTextLimit(Integer.toString(latitudeSpinner.getMaximum()).length() + 1);
		if (coordinates != null) {
			latitudeSpinner.setSelection((int) coordinates.getLatitude() * FACTOR);
		}
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeSpinner);
		final Label latitudeDegreeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(latitudeDegreeLabel);
		latitudeDegreeLabel.setText(DEGREE_SIGN);
		final Combo latitudeCombo = new Combo(coordinatesGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(latitudeCombo);
		latitudeCombo.setItems("N", "S");
		latitudeCombo.select(0);

		final Label longitudeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(longitudeLabel);
		longitudeLabel.setText(Messages.get("lbl.feregion.dialog.longitude"));
		final Spinner longitudeSpinner = new Spinner(coordinatesGroup, SWT.BORDER);
		longitudeSpinner.setDigits(DIGITS);
		longitudeSpinner.setMinimum(LONGITUDE_MIN_VALUE * FACTOR);
		longitudeSpinner.setMaximum(LONGITUDE_MAX_VALUE * FACTOR);
		longitudeSpinner.setIncrement(FACTOR);
		longitudeSpinner.setTextLimit(Integer.toString(longitudeSpinner.getMaximum()).length() + 1);
		if (coordinates != null) {
			longitudeSpinner.setSelection((int) coordinates.getLongitude() * FACTOR);
		}
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeSpinner);
		final Label longitudeDegreeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(longitudeDegreeLabel);
		longitudeDegreeLabel.setText(DEGREE_SIGN);
		final Combo longitudeCombo = new Combo(coordinatesGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(longitudeCombo);
		longitudeCombo.setItems("E", "W");
		longitudeCombo.select(0);

		final Group regionGroup = new Group(dialogArea, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(regionGroup);
		GridLayoutFactory.swtDefaults().numColumns(4).applyTo(regionGroup);
		regionGroup.setText(Messages.get("lbl.feregion.dialog.region"));

		final Label regionNumberLabel = new Label(regionGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(regionNumberLabel);
		regionNumberLabel.setText(Messages.get("lbl.feregion.dialog.region.number"));

		regionNumberText = new Text(regionGroup, SWT.READ_ONLY | SWT.BORDER | SWT.RIGHT);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(false, false).applyTo(regionNumberText);
		regionNumberText.setEditable(false);
		regionNumberText.setText("000");

		final Label regionNameLabel = new Label(regionGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(regionNameLabel);
		regionNameLabel.setText(Messages.get("lbl.feregion.dialog.region.name"));

		regionNameText = new Text(regionGroup, SWT.READ_ONLY | SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).applyTo(regionNameText);
		regionNameText.setEditable(false);

		browser = new Browser(regionGroup, SWT.NONE);
		GridDataFactory.swtDefaults().span(4, 1).align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(browser);
 		browser.setUrl(getMapPage(shell, LeafletMapDialog.class.getResourceAsStream(HTML_FILE_NAME)).toString());

		final Composite buttonBar = new Composite(shell, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(buttonBar);
		GridLayoutFactory.swtDefaults().applyTo(buttonBar);

		final Button closeButton = new Button(buttonBar, SWT.PUSH);
		final int buttonWidth = SwtUtils.convertHorizontalDLUsToPixels(closeButton, IDialogConstants.BUTTON_WIDTH);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).minSize(buttonWidth, SWT.DEFAULT).applyTo(closeButton);
		closeButton.setText(Messages.get("lbl.button.close"));
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				shell.close();
			}
		});

		final ModifyListener modifyListener = e -> {
			final String longitude = longitudeSpinner.getSelection() / (double) FACTOR + longitudeCombo.getText();
			final String latitude = latitudeSpinner.getSelection() / (double) FACTOR + latitudeCombo.getText();
			final Coordinates currentCoordinates = Coordinates.parse(longitude, latitude);
			if (!currentCoordinates.equals(coordinates)) {
				this.coordinates = currentCoordinates;
				setResult();
			}
		};
		latitudeSpinner.addModifyListener(modifyListener);
		latitudeCombo.addModifyListener(modifyListener);
		longitudeSpinner.addModifyListener(modifyListener);
		longitudeCombo.addModifyListener(modifyListener);

		shell.pack();
		final Point size = shell.getSize();
		shell.setMinimumSize(size);
		final int max = Math.max(size.x, size.y);
		shell.setSize(max, max);

		if (coordinates != null) {
			setResult();
		}
		else {
			modifyListener.modifyText(null);
		}

		shell.open();
	}

	private void setResult() {
		logger.log(Level.FINE, "{0}", coordinates);
		final Region region = feregion.getGeographicRegion(coordinates);
		regionNumberText.setText(Integer.toString(region.getNumber()));
		regionNameText.setText(region.getName());
		final int a = (int) coordinates.getLatitude();
		final int b = (int) coordinates.getLongitude();
		final int c = a + (a < 0 ? -1 : 1);
		final int d = b + (b < 0 ? -1 : 1);
		final float e = a + (a < 0 ? -0.5f : 0.5f);
		final float f = b + (b < 0 ? -0.5f : 0.5f);
		logger.log(Level.FINE, "{0}, {1}, {2}, {3}, {4}, {5}", new Number[] { a, b, c, d, e, f });
		browser.execute(String.format("if (window.rect) { window.rect.remove(); } window.rect = L.rectangle(([[%s, %s], [%s, %s]]), {color: '#ff7800', weight: 1}); window.rect.addTo(map); map.flyTo(new L.LatLng(%s, %s), 6);", a, b, c, d, e, f));
	}

}
