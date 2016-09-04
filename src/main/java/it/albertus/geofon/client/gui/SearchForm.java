package it.albertus.geofon.client.gui;

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

	private Label periodLabel;
	private Label periodFromLabel;
	private Label periodToLabel;
	private Text periodFromText;
	private Text periodToText;
	private Label periodFromNote;
	private Label periodToNote;
	private Label latitudeLabel;
	private Label latitudeFromLabel;
	private Text latitudeFromText;
	private Label latitudeFromNote;
	private Label latitudeToLabel;
	private Text latitudeToText;
	private Label latitudeToNote;
	private Label longitudeLabel;
	private Label longitudeFromLabel;
	private Text longitudeFromText;
	private Label longitudeFromNote;
	private Label longitudeToLabel;
	private Text longitudeToText;
	private Label longitudeToNote;
	private Label minimumMagnitudeLabel;
	private Label minimumMagnitudeFromLabel;
	private Text minimumMagnitudeFromText;
	private Label minimumMagnitudeToText;
	private Button restrictButton;
	private Label restrictLabel;
	private Label outputFormatLabel;
	private Map<String, Button> modeRadios = new LinkedHashMap<String, Button>();
	private Label resultsLabel;
	private Text resultsText;
	private Button searchButton;
	private Composite composite;

	public SearchForm(final GeofonClientGui gui) {
		composite = new Composite(gui.getShell(), SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(7).applyTo(composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		periodLabel = new Label(composite, SWT.NONE);
		periodLabel.setText("Time Period");
		periodFromLabel = new Label(composite, SWT.NONE);
		periodFromLabel.setText("from");
		periodFromText = new Text(composite, SWT.BORDER);
		periodFromNote = new Label(composite, SWT.NONE);
		periodFromNote.setText("(yyyy-mm-dd)");
		periodToLabel = new Label(composite, SWT.NONE);
		periodToLabel.setText("to");
		periodToText = new Text(composite, SWT.BORDER);
		periodToNote = new Label(composite, SWT.NONE);
		periodToNote.setText("(yyyy-mm-dd)");

		latitudeLabel = new Label(composite, SWT.NONE);
		latitudeLabel.setText("Latitude range");
		latitudeFromLabel = new Label(composite, SWT.NONE);
		latitudeFromLabel.setText("from");
		latitudeFromText = new Text(composite, SWT.BORDER);
		latitudeFromNote = new Label(composite, SWT.NONE);
		latitudeFromNote.setText("\u00B0 (southern limit)");
		latitudeToLabel = new Label(composite, SWT.NONE);
		latitudeToLabel.setText("to");
		latitudeToText = new Text(composite, SWT.BORDER);
		latitudeToNote = new Label(composite, SWT.NONE);
		latitudeToNote.setText("\u00B0 (northern limit)");

		longitudeLabel = new Label(composite, SWT.NONE);
		longitudeLabel.setText("Longitude range");
		longitudeFromLabel = new Label(composite, SWT.NONE);
		longitudeFromLabel.setText("from");
		longitudeFromText = new Text(composite, SWT.BORDER);
		longitudeFromNote = new Label(composite, SWT.NONE);
		longitudeFromNote.setText("\u00B0 (western limit)");
		longitudeToLabel = new Label(composite, SWT.NONE);
		longitudeToLabel.setText("to");
		longitudeToText = new Text(composite, SWT.BORDER);
		longitudeToNote = new Label(composite, SWT.NONE);
		longitudeToNote.setText("\u00B0 (eastern limit)");

		minimumMagnitudeLabel = new Label(composite, SWT.NONE);
		minimumMagnitudeLabel.setText("Minimum magnitude");
		minimumMagnitudeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		//		minimumMagnitudeFromLabel = new Label(parent, SWT.NONE);
		minimumMagnitudeFromText = new Text(composite, SWT.BORDER);

		Composite restrictComposite = new Composite(composite, SWT.NONE);
		restrictComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		restrictButton = new Button(restrictComposite, SWT.CHECK);
		restrictLabel = new Label(restrictComposite, SWT.NONE);
		restrictLabel.setText("Restrict to events with moment tensors");
		//		GridDataFactory.swtDefaults().span(4, 1).applyTo(restrictComposite);
		restrictComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 1));

		outputFormatLabel = new Label(composite, SWT.NONE);
		outputFormatLabel.setText("Output format");
		final Composite radioComposite = new Composite(composite, SWT.NONE);
		radioComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		radioComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		for (final String mode : new String[] { "RSS", "KML" }) {
			final Button radio = new Button(radioComposite, SWT.RADIO);
			radio.setSelection("RSS".equals(mode));
			radio.setText(mode);
			//			radio.addSelectionListener(new ModeRadioSelectionListener(this, radio, mode));
			modeRadios.put(mode, radio);
		}
		resultsLabel = new Label(composite, SWT.NONE);
		resultsLabel.setText("Limit results to");
		resultsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		resultsText = new Text(composite, SWT.BORDER);

		searchButton = new Button(composite, SWT.NULL);
		searchButton.setText("Search");
		//		searchButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		searchButton.addSelectionListener(new SearchButtonSelectionListener(gui));
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

	public Label getMinimumMagnitudeFromLabel() {
		return minimumMagnitudeFromLabel;
	}

	public Text getMinimumMagnitudeFromText() {
		return minimumMagnitudeFromText;
	}

	public Label getMinimumMagnitudeToText() {
		return minimumMagnitudeToText;
	}

	public Button getRestrictButton() {
		return restrictButton;
	}

	public Label getRestrictLabel() {
		return restrictLabel;
	}

	public Label getOutputFormatLabel() {
		return outputFormatLabel;
	}

	public Map<String, Button> getModeRadios() {
		return modeRadios;
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
