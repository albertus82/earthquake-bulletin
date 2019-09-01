package it.albertus.eqbulletin.gui;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import gov.usgs.cr.hazards.feregion.fe_1995.Coordinates;
import gov.usgs.cr.hazards.feregion.fe_1995.FERegion;
import gov.usgs.cr.hazards.feregion.fe_1995.LongitudeRange;
import gov.usgs.cr.hazards.feregion.fe_1995.Region;
import it.albertus.eqbulletin.resources.Leaflet;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.maps.CoordinateUtils;
import it.albertus.jface.maps.MapBounds;
import it.albertus.jface.maps.MapDialog;
import it.albertus.jface.maps.leaflet.LeafletMapControl;
import it.albertus.jface.maps.leaflet.LeafletMapDialog;
import it.albertus.jface.maps.leaflet.LeafletMapOptions;
import it.albertus.util.logging.LoggerFactory;

public class FERegionDialog extends Dialog {

	private static final Logger logger = LoggerFactory.getLogger(FERegionDialog.class);

	private static final int LATITUDE_MIN_VALUE = 0;
	private static final int LATITUDE_MAX_VALUE = MapBounds.LATITUDE_MAX_VALUE;
	private static final int LONGITUDE_MIN_VALUE = 0;
	private static final int LONGITUDE_MAX_VALUE = MapBounds.LONGITUDE_MAX_VALUE;

	private static final short DIGITS = 2;
	private static final short FACTOR = 100;

	private static final String MAP_ONCLICK_FN = "mapOnClick";
	private static final String MAP_ONLOAD_FN = "mapOnLoad";

	private final FERegion feregion;

	private Coordinates coordinates;
	private Collection<Rectangle> rectangles;

	private Spinner latitudeSpinner;
	private Combo latitudeCombo;
	private Spinner longitudeSpinner;
	private Combo longitudeCombo;
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
			throw new UncheckedIOException(e);
		}
		setText(Messages.get("lbl.feregion.dialog.title"));
	}

	public FERegionDialog(final Shell parent) {
		this(parent, null);
	}

	public void open() {
		final Shell shell = new Shell(getParent(), SWT.APPLICATION_MODAL | SWT.CLOSE | SWT.RESIZE | SWT.MAX);
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
		latitudeSpinner = new Spinner(coordinatesGroup, SWT.BORDER);
		latitudeSpinner.setDigits(DIGITS);
		latitudeSpinner.setMinimum(LATITUDE_MIN_VALUE * FACTOR);
		latitudeSpinner.setMaximum(LATITUDE_MAX_VALUE * FACTOR);
		latitudeSpinner.setIncrement(FACTOR);
		latitudeSpinner.setTextLimit(Integer.toString(latitudeSpinner.getMaximum()).length() + 1);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeSpinner);
		final Label latitudeDegreeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(latitudeDegreeLabel);
		latitudeDegreeLabel.setText(CoordinateUtils.DEGREE_SIGN);
		latitudeCombo = new Combo(coordinatesGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(latitudeCombo);
		latitudeCombo.setItems("N", "S");
		latitudeCombo.select(0);

		final Label longitudeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(longitudeLabel);
		longitudeLabel.setText(Messages.get("lbl.feregion.dialog.longitude"));
		longitudeSpinner = new Spinner(coordinatesGroup, SWT.BORDER);
		longitudeSpinner.setDigits(DIGITS);
		longitudeSpinner.setMinimum(LONGITUDE_MIN_VALUE * FACTOR);
		longitudeSpinner.setMaximum(LONGITUDE_MAX_VALUE * FACTOR);
		longitudeSpinner.setIncrement(FACTOR);
		longitudeSpinner.setTextLimit(Integer.toString(longitudeSpinner.getMaximum()).length() + 1);

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeSpinner);
		final Label longitudeDegreeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(longitudeDegreeLabel);
		longitudeDegreeLabel.setText(CoordinateUtils.DEGREE_SIGN);
		longitudeCombo = new Combo(coordinatesGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
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

		final BrowserFunction mapOnClick = new BrowserFunction(browser, MAP_ONCLICK_FN) {
			@Override
			public Object function(final Object[] args) {
				final double latitude = ((Number) args[0]).doubleValue();
				double longitude = ((Number) args[1]).doubleValue();
				while (Math.abs(longitude) > LONGITUDE_MAX_VALUE) {
					longitude -= Math.signum(longitude) * LONGITUDE_MAX_VALUE * 2;
				}
				setInputFields(latitude, longitude);
				return null;
			}
		};
		browser.addDisposeListener(e -> mapOnClick.dispose());

		final LeafletMapOptions options = new LeafletMapOptions();
		options.getControls().put(LeafletMapControl.ZOOM, "");
		options.getControls().put(LeafletMapControl.ATTRIBUTION, "");
		options.getControls().put(LeafletMapControl.SCALE, "");
		if (Leaflet.LAYERS != null && !Leaflet.LAYERS.isEmpty()) {
			options.getControls().put(LeafletMapControl.LAYERS, Leaflet.LAYERS);
		}

		final StringBuilder other = new StringBuilder("map.on('click', function(e) { ").append(MAP_ONCLICK_FN).append("(e.latlng.lat, e.latlng.lng); }); ");

		if (coordinates != null) {
			setInputFields(coordinates.getLatitude(), coordinates.getLongitude());
			final BrowserFunction mapOnLoad = new BrowserFunction(browser, MAP_ONLOAD_FN) {
				@Override
				public Object function(final Object[] args) {
					setResult();
					return null;
				}
			};
			browser.addDisposeListener(e -> mapOnLoad.dispose());
			options.setCenterLat(coordinates.getLatitude());
			options.setCenterLng(coordinates.getLongitude());
			options.setZoom(5);
			other.append("document.onload = ").append(MAP_ONLOAD_FN).append("();");
		}

		try (final InputStream is = LeafletMapDialog.class.getResourceAsStream("map.html")) {
			browser.setUrl(MapDialog.getMapPage(regionGroup.getShell(), is, line -> LeafletMapDialog.parseLine(line, options, Collections.emptySet(), other.toString())).toString());
		}
		catch (final IOException e) {
			throw new UncheckedIOException(e);
		}

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
		shell.setSize(max, (int) (max * 1.33f));

		regionNumberText.setText("");

		browser.setFocus();

		shell.open();
	}

	private void setResult() {
		logger.log(Level.FINE, "{0}", coordinates);
		final Region region = feregion.getGeographicRegion(coordinates);
		setResultFields(region);
		setPolygonOnMap(region);
	}

	private void setInputFields(final double latitude, final double longitude) {
		latitudeSpinner.setSelection((int) (Math.abs(latitude) * FACTOR));
		latitudeCombo.setText(latitude < 0 ? "S" : "N");
		longitudeSpinner.setSelection((int) (Math.abs(longitude) * FACTOR));
		longitudeCombo.setText(longitude < 0 ? "W" : "E");
	}

	private void setResultFields(final Region region) {
		regionNumberText.setText(Integer.toString(region.getNumber()));
		regionNameText.setText(region.getName());
	}

	private void setPolygonOnMap(final Region region) {
		final Map<Integer, Set<LongitudeRange>> map = feregion.getLatitudeLongitudeMap(region.getNumber());
		logger.log(Level.FINE, "Latitude/longitude map for {0} {1}: {2}", new Object[] { region.getNumber(), region.getName(), map });
		final Collection<Rectangle> rects = new LinkedHashSet<>();
		for (final Entry<Integer, Set<LongitudeRange>> entry : map.entrySet()) {
			final int latitude = entry.getKey();
			for (final LongitudeRange range : entry.getValue()) {
				final int a = latitude >= 0 ? latitude - 1 : latitude;
				final int b = range.getFrom();
				final int c = latitude < 0 ? latitude + 1 : latitude;
				final int d = range.getTo();
				rects.add(new Rectangle(a, b, c, d));
			}
		}
		logger.log(Level.FINE, "Rectangles: {0}", rects);

		final StringBuilder script = new StringBuilder();
		if (!rects.equals(this.rectangles)) {
			this.rectangles = rects;
			script.append("if (window.rects) { for (var i = 0; i < window.rects.length; i++) { window.rects[i].remove(); } }; window.rects = [];").append(System.lineSeparator());
			for (final Rectangle rect : rects) {
				script.append("window.rects.push(L.rectangle(([[").append(rect.a).append(", ").append(rect.b).append("], [").append(rect.c).append(", ").append(rect.d).append("]]), { color: 'red', weight: 0, smoothFactor: 0.95 }).addTo(map));").append(System.lineSeparator());
			}
		}
		script.append("map.flyTo(new L.LatLng(").append(coordinates.getLatitude()).append(", ").append(coordinates.getLongitude()).append("));");
		logger.log(Level.FINE, "Executing script:{0}{1}", new Object[] { System.lineSeparator(), script });
		browser.execute(script.toString());
	}

	private static class Rectangle {

		private final int a;
		private final int b;
		private final int c;
		private final int d;

		private Rectangle(final int a, final int b, final int c, final int d) {
			this.a = a;
			this.b = b;
			this.c = c;
			this.d = d;
		}

		@Override
		public int hashCode() {
			return Objects.hash(a, b, c, d);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Rectangle)) {
				return false;
			}
			final Rectangle other = (Rectangle) obj;
			return a == other.a && b == other.b && c == other.c && d == other.d;
		}

		@Override
		public String toString() {
			return Arrays.toString(new int[] { a, b, c, d });
		}
	}

}
