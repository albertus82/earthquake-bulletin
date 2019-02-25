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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
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
import it.albertus.jface.listener.ShortVerifyListener;
import it.albertus.jface.validation.ShortTextValidator;
import it.albertus.jface.validation.Validator;

public class FERegionDialog extends Dialog {

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
		final VerifyListener coordinatesVerifyListener = new ShortVerifyListener(false);

		final Collection<Validator> validators = new ArrayList<>();

		final Shell shell = new Shell(getParent(), SWT.CLOSE);
		shell.setText(getText());
		shell.setImages(Images.getMainIconArray());
		GridLayoutFactory.swtDefaults().applyTo(shell);

		final Composite dialogArea = new Composite(shell, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).applyTo(dialogArea);
		GridLayoutFactory.swtDefaults().numColumns(3).applyTo(dialogArea);

		final Label latitudeLabel = new Label(dialogArea, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(latitudeLabel);
		latitudeLabel.setText(Messages.get("lbl.feregion.dialog.latitude"));
		final Text latitudeText = new Text(dialogArea, SWT.BORDER);
		latitudeText.setTextLimit(2);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeText);
		final ShortTextValidator latitudeValidator = new ShortTextValidator(latitudeText, false, (short) 0, (short) 90);
		validators.add(latitudeValidator);
		new FormControlValidatorDecoration(latitudeValidator, JFaceMessages.get("err.preferences.integer.range", 0, 90));
		latitudeText.addVerifyListener(coordinatesVerifyListener);
		final Combo latitudeCombo = new Combo(dialogArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(latitudeCombo);
		latitudeCombo.setData(Integer.toString(0), 'N');
		latitudeCombo.setData(Integer.toString(1), 'S');
		latitudeCombo.add("\u00B0N", 0);
		latitudeCombo.add("\u00B0S", 1);
		latitudeCombo.select(0);

		final Label longitudeLabel = new Label(dialogArea, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(longitudeLabel);
		longitudeLabel.setText(Messages.get("lbl.feregion.dialog.longitude"));
		final Text longitudeText = new Text(dialogArea, SWT.BORDER);
		longitudeText.setTextLimit(3);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeText);
		final ShortTextValidator longitudeValidator = new ShortTextValidator(longitudeText, false, (short) 0, (short) 180);
		validators.add(longitudeValidator);
		new FormControlValidatorDecoration(longitudeValidator, JFaceMessages.get("err.preferences.integer.range", 0, 180));
		longitudeText.addVerifyListener(coordinatesVerifyListener);
		final Combo longitudeCombo = new Combo(dialogArea, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(false, false).applyTo(longitudeCombo);
		longitudeCombo.setData(Integer.toString(0), 'E');
		longitudeCombo.setData(Integer.toString(1), 'W');
		longitudeCombo.add("\u00B0E", 0);
		longitudeCombo.add("\u00B0W", 1);
		longitudeCombo.select(0);

		final Label regionLabel = new Label(dialogArea, SWT.NONE);
		GridDataFactory.swtDefaults().applyTo(regionLabel);
		regionLabel.setText(Messages.get("lbl.feregion.dialog.region"));
		final Text regionText = new Text(dialogArea, SWT.READ_ONLY | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL);
		final int horizontalIndent = ((GridData) longitudeText.getLayoutData()).horizontalIndent;
		GridDataFactory.swtDefaults().span(2, 1).align(SWT.FILL, SWT.CENTER).grab(true, false).indent(horizontalIndent, SWT.DEFAULT).hint(SwtUtils.convertHorizontalDLUsToPixels(regionText, 200), regionText.getLineHeight() * 5 + 1).applyTo(regionText);
		regionText.setEditable(false);
		regionText.setFont(JFaceResources.getTextFont());

		final Composite buttonBar = new Composite(shell, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(buttonBar);
		GridLayoutFactory.swtDefaults().numColumns(1).applyTo(buttonBar);

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
					return;
				}
			}
			final String longitude = longitudeText.getText() + longitudeCombo.getData(Integer.toString(longitudeCombo.getSelectionIndex()));
			final String latitude = latitudeText.getText() + latitudeCombo.getData(Integer.toString(latitudeCombo.getSelectionIndex()));
			regionText.setText(buildRegionText(longitude, latitude));
		};
		latitudeText.addModifyListener(modifyListener);
		latitudeCombo.addModifyListener(modifyListener);
		longitudeText.addModifyListener(modifyListener);
		longitudeCombo.addModifyListener(modifyListener);

		shell.pack();
		shell.open();
	}

	private String buildRegionText(final String longitude, final String latitude) {
		final Map<FEPlusNameType, String> map = feplusnames.getNames().get(feregion.getGeographicRegionNumber(Coordinates.parse(longitude, latitude)));
		final StringBuilder sb = new StringBuilder();
		for (final Entry<FEPlusNameType, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append(" > ").append(entry.getValue()).append(System.lineSeparator());
		}
		return sb.toString().trim();
	}

}
