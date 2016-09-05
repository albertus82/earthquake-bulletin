package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.listener.FormatRadioSelectionListener;
import it.albertus.geofon.client.gui.listener.SearchButtonSelectionListener;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SearchForm {

	private final Composite composite;

	private final Label periodLabel;
	private final Label periodFromLabel;
	private final Label periodToLabel;
	private final Text periodFromText;
	private final Text periodToText;
	private final Label periodFromNote;
	private final Label periodToNote;

	private final Label latitudeLabel;
	private final Label latitudeFromLabel;
	private final Text latitudeFromText;
	private final Label latitudeFromNote;
	private final Label latitudeToLabel;
	private final Text latitudeToText;
	private final Label latitudeToNote;

	private final Label longitudeLabel;
	private final Label longitudeFromLabel;
	private final Text longitudeFromText;
	private final Label longitudeFromNote;
	private final Label longitudeToLabel;
	private final Text longitudeToText;
	private final Label longitudeToNote;

	private final Label minimumMagnitudeLabel;
	private final Text minimumMagnitudeText;
	private final Button restrictButton;

	private final Label outputFormatLabel;
	private final Map<String, Button> formatRadios = new LinkedHashMap<String, Button>();
	private final Label resultsLabel;
	private final Text resultsText;

	private final Button searchButton;

	public SearchForm(final GeofonClientGui gui) {
		composite = new Composite(gui.getShell(), SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(7).applyTo(composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		periodLabel = new Label(composite, SWT.NONE);
		periodLabel.setText("Time Period");
		periodFromLabel = new Label(composite, SWT.NONE);
		periodFromLabel.setText("from");
		periodFromText = new Text(composite, SWT.BORDER);
		periodFromText.setTextLimit(10);
		periodFromNote = new Label(composite, SWT.NONE);
		periodFromNote.setText("(yyyy-mm-dd)");
		periodToLabel = new Label(composite, SWT.NONE);
		periodToLabel.setText("to");
		periodToText = new Text(composite, SWT.BORDER);
		periodToText.setTextLimit(10);
		periodToNote = new Label(composite, SWT.NONE);
		periodToNote.setText("(yyyy-mm-dd)");

		latitudeLabel = new Label(composite, SWT.NONE);
		latitudeLabel.setText("Latitude range");
		latitudeFromLabel = new Label(composite, SWT.NONE);
		latitudeFromLabel.setText("from");
		latitudeFromText = new Text(composite, SWT.BORDER);
		latitudeFromText.setTextLimit(7);
		latitudeFromNote = new Label(composite, SWT.NONE);
		latitudeFromNote.setText("\u00B0 (southern limit)");
		latitudeToLabel = new Label(composite, SWT.NONE);
		latitudeToLabel.setText("to");
		latitudeToText = new Text(composite, SWT.BORDER);
		latitudeToText.setTextLimit(7);
		latitudeToNote = new Label(composite, SWT.NONE);
		latitudeToNote.setText("\u00B0 (northern limit)");

		longitudeLabel = new Label(composite, SWT.NONE);
		longitudeLabel.setText("Longitude range");
		longitudeFromLabel = new Label(composite, SWT.NONE);
		longitudeFromLabel.setText("from");
		longitudeFromText = new Text(composite, SWT.BORDER);
		longitudeFromText.setTextLimit(7);
		longitudeFromNote = new Label(composite, SWT.NONE);
		longitudeFromNote.setText("\u00B0 (western limit)");
		longitudeToLabel = new Label(composite, SWT.NONE);
		longitudeToLabel.setText("to");
		longitudeToText = new Text(composite, SWT.BORDER);
		longitudeToText.setTextLimit(7);
		longitudeToNote = new Label(composite, SWT.NONE);
		longitudeToNote.setText("\u00B0 (eastern limit)");

		minimumMagnitudeLabel = new Label(composite, SWT.NONE);
		minimumMagnitudeLabel.setText("Minimum magnitude");
		minimumMagnitudeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		minimumMagnitudeText = new Text(composite, SWT.BORDER);
		minimumMagnitudeText.setTextLimit(3);

		restrictButton = new Button(composite, SWT.CHECK);
		restrictButton.setText("Restrict to events with moment tensors");
		restrictButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 1));

		outputFormatLabel = new Label(composite, SWT.NONE);
		outputFormatLabel.setText("Output format");
		final Composite radioComposite = new Composite(composite, SWT.NONE);
		radioComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		radioComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		for (final String mode : new String[] { "RSS", "KML" }) {
			final Button radio = new Button(radioComposite, SWT.RADIO);
			radio.addSelectionListener(new FormatRadioSelectionListener(this, radio, mode));
			radio.setText(mode);
			radio.setSelection("RSS".equals(mode));
			radio.setEnabled("RSS".equals(mode));
			formatRadios.put(mode, radio);
		}
		resultsLabel = new Label(composite, SWT.NONE);
		resultsLabel.setText("Limit results to");
		resultsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		resultsText = new Text(composite, SWT.BORDER);
		resultsText.setTextLimit(3);

		searchButton = new Button(composite, SWT.NULL);
		searchButton.setText("Search");
		searchButton.addSelectionListener(new SearchButtonSelectionListener(gui));
	}
	
	public void disableControls() {
		searchButton.setEnabled(false);
	}
	
	public void enableControls() {
		searchButton.setEnabled(true);
	}

	public Composite getComposite() {
		return composite;
	}

	public Label getPeriodLabel() {
		return periodLabel;
	}

	public Label getPeriodFromLabel() {
		return periodFromLabel;
	}

	public Label getPeriodToLabel() {
		return periodToLabel;
	}

	public Text getPeriodFromText() {
		return periodFromText;
	}

	public Text getPeriodToText() {
		return periodToText;
	}

	public Label getPeriodFromNote() {
		return periodFromNote;
	}

	public Label getPeriodToNote() {
		return periodToNote;
	}

	public Label getLatitudeLabel() {
		return latitudeLabel;
	}

	public Label getLatitudeFromLabel() {
		return latitudeFromLabel;
	}

	public Text getLatitudeFromText() {
		return latitudeFromText;
	}

	public Label getLatitudeFromNote() {
		return latitudeFromNote;
	}

	public Label getLatitudeToLabel() {
		return latitudeToLabel;
	}

	public Text getLatitudeToText() {
		return latitudeToText;
	}

	public Label getLatitudeToNote() {
		return latitudeToNote;
	}

	public Label getLongitudeLabel() {
		return longitudeLabel;
	}

	public Label getLongitudeFromLabel() {
		return longitudeFromLabel;
	}

	public Text getLongitudeFromText() {
		return longitudeFromText;
	}

	public Label getLongitudeFromNote() {
		return longitudeFromNote;
	}

	public Label getLongitudeToLabel() {
		return longitudeToLabel;
	}

	public Text getLongitudeToText() {
		return longitudeToText;
	}

	public Label getLongitudeToNote() {
		return longitudeToNote;
	}

	public Label getMinimumMagnitudeLabel() {
		return minimumMagnitudeLabel;
	}

	public Text getMinimumMagnitudeText() {
		return minimumMagnitudeText;
	}

	public Button getRestrictButton() {
		return restrictButton;
	}

	public Label getOutputFormatLabel() {
		return outputFormatLabel;
	}

	public Map<String, Button> getFormatRadios() {
		return formatRadios;
	}

	public Label getResultsLabel() {
		return resultsLabel;
	}

	public Text getResultsText() {
		return resultsText;
	}

	public Button getSearchButton() {
		return searchButton;
	}

}
