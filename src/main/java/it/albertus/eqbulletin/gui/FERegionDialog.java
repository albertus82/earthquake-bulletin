package it.albertus.eqbulletin.gui;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import gov.usgs.cr.hazards.feregion.fe_1995.Coordinates;
import gov.usgs.cr.hazards.feregion.fe_1995.FERegion;
import gov.usgs.cr.hazards.feregion.feplus.source.FEPlusNameType;
import gov.usgs.cr.hazards.feregion.feplus.source.FEPlusNames;
import it.albertus.eqbulletin.gui.decoration.FormControlValidatorDecoration;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.listener.FloatVerifyListener;
import it.albertus.jface.validation.FloatTextValidator;
import it.albertus.jface.validation.Validator;

public class FERegionDialog extends Dialog {

	private static final float LATITUDE_MIN_VALUE = 0f;
	private static final float LATITUDE_MAX_VALUE = 90f;
	private static final float LONGITUDE_MIN_VALUE = 0f;
	private static final float LONGITUDE_MAX_VALUE = 180f;

	private static final byte REGION_TEXT_HEIGHT = 5;

	private static final String DEGREE_SIGN = "\u00B0";

	private final FERegion feregion;
	private final FEPlusNames feplusnames;

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
		final VerifyListener coordinatesVerifyListener = new FloatVerifyListener(false);

		final Collection<Validator> validators = new ArrayList<>();

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
		final Text latitudeText = new Text(coordinatesGroup, SWT.BORDER);
		latitudeText.setTextLimit("90.00".length());
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeText);
		final FloatTextValidator latitudeValidator = new FloatTextValidator(latitudeText, false, LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE);
		validators.add(latitudeValidator);
		new FormControlValidatorDecoration(latitudeValidator, JFaceMessages.get("err.preferences.decimal.range", LATITUDE_MIN_VALUE, LATITUDE_MAX_VALUE));
		latitudeText.addVerifyListener(coordinatesVerifyListener);
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
		final Text longitudeText = new Text(coordinatesGroup, SWT.BORDER);
		longitudeText.setTextLimit("180.00".length());
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeText);
		final FloatTextValidator longitudeValidator = new FloatTextValidator(longitudeText, false, LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE);
		validators.add(longitudeValidator);
		new FormControlValidatorDecoration(longitudeValidator, JFaceMessages.get("err.preferences.decimal.range", LONGITUDE_MIN_VALUE, LONGITUDE_MAX_VALUE));
		longitudeText.addVerifyListener(coordinatesVerifyListener);
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
		final String defaultMessage = "-- " + Messages.get("msg.feregion.dialog.coordinates") + " --";
		regionText.setText(defaultMessage);

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
			for (final Validator validator : validators) {
				if (!validator.isValid()) {
					regionText.setText(defaultMessage);
					return;
				}
			}
			final String longitude = longitudeText.getText() + longitudeCombo.getText();
			final String latitude = latitudeText.getText() + latitudeCombo.getText();
			regionText.setText(buildRegionText(longitude, latitude));
		};
		latitudeText.addModifyListener(modifyListener);
		latitudeCombo.addModifyListener(modifyListener);
		longitudeText.addModifyListener(modifyListener);
		longitudeCombo.addModifyListener(modifyListener);

		shell.pack();
		shell.setMinimumSize(shell.getSize());
		shell.open();
	}

	private String buildRegionText(final String longitude, final String latitude) {
		final Map<FEPlusNameType, String> map = feplusnames.getNames().get(feregion.getGeographicRegionNumber(Coordinates.parse(longitude, latitude)));
		final StringBuilder sb = new StringBuilder();
		for (final Entry<FEPlusNameType, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append(" \u2192 ").append(entry.getValue()).append(System.lineSeparator());
		}
		return sb.toString().trim();
	}

}
