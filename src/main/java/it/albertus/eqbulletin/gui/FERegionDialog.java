package it.albertus.eqbulletin.gui;

import java.io.IOError;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import gov.usgs.cr.hazards.feregion.feplus.source.FEPlusNameType;
import gov.usgs.cr.hazards.feregion.feplus.source.FEPlusNames;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.SwtUtils;
import it.albertus.util.logging.LoggerFactory;

public class FERegionDialog extends Dialog {

	private static final Logger logger = LoggerFactory.getLogger(FERegionDialog.class);

	private static final short LATITUDE_MIN_VALUE = 0;
	private static final short LATITUDE_MAX_VALUE = 90;
	private static final short LONGITUDE_MIN_VALUE = 0;
	private static final short LONGITUDE_MAX_VALUE = 180;

	private static final short DIGITS = 2;
	private static final short FACTOR = 100;

	private static final byte REGION_TEXT_HEIGHT = 5;

	private static final String DEGREE_SIGN = "\u00B0";

	private final FERegion feregion;
	private final FEPlusNames feplusnames;

	private Coordinates coordinates;

	public FERegionDialog(final Shell parent) {
		super(parent);
		try {
			feregion = new FERegion();
			feplusnames = new FEPlusNames();
		}
		catch (final IOException e) {
			throw new IOError(e);
		}
		setText(Messages.get("lbl.feregion.dialog.title"));
	}

	public void open() {
		final Shell shell = new Shell(getParent(), SWT.CLOSE | SWT.RESIZE);
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
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeSpinner);
		final Label longitudeDegreeLabel = new Label(coordinatesGroup, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(longitudeDegreeLabel);
		longitudeDegreeLabel.setText(DEGREE_SIGN);
		final Combo longitudeCombo = new Combo(coordinatesGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(longitudeCombo);
		longitudeCombo.setItems("E", "W");
		longitudeCombo.select(0);

		final Text regionText = new Text(dialogArea, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).hint(SWT.DEFAULT, regionText.getLineHeight() * REGION_TEXT_HEIGHT + 1).applyTo(regionText);
		regionText.setEditable(false);
		regionText.setFont(JFaceResources.getTextFont());

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
			final String longitude = longitudeSpinner.getSelection() / (float) FACTOR + longitudeCombo.getText();
			final String latitude = latitudeSpinner.getSelection() / (float) FACTOR + latitudeCombo.getText();
			final Coordinates currentCoordinates = Coordinates.parse(longitude, latitude);
			if (!currentCoordinates.equals(coordinates)) {
				this.coordinates = currentCoordinates;
				regionText.setText(buildRegionText());
			}
		};
		latitudeSpinner.addModifyListener(modifyListener);
		latitudeCombo.addModifyListener(modifyListener);
		longitudeSpinner.addModifyListener(modifyListener);
		longitudeCombo.addModifyListener(modifyListener);

		modifyListener.modifyText(null);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.open();
	}

	private String buildRegionText() {
		logger.log(Level.FINE, "{0}", coordinates);
		final Map<FEPlusNameType, String> map = feplusnames.getNames().get(feregion.getGeographicRegionNumber(coordinates));
		final StringBuilder sb = new StringBuilder();
		for (final Entry<FEPlusNameType, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append(" \u2192 ").append(entry.getValue()).append(System.lineSeparator());
		}
		return sb.toString().trim();
	}

}
