package it.albertus.geofon.client.gui;

import java.util.LinkedHashMap;
import java.util.Map;

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

	public SearchForm(final Composite parent) {
		periodLabel = new Label(parent, SWT.NONE);
		periodLabel.setText("Time Period");
		periodFromLabel = new Label(parent, SWT.NONE);
		periodFromLabel.setText("from");
		periodFromText = new Text(parent, SWT.BORDER);
		periodFromNote = new Label(parent, SWT.NONE);
		periodFromNote.setText("(yyyy-mm-dd)");
		periodToLabel = new Label(parent, SWT.NONE);
		periodToLabel.setText("to");
		periodToText = new Text(parent, SWT.BORDER);
		periodToNote = new Label(parent, SWT.NONE);
		periodToNote.setText("(yyyy-mm-dd)");

		latitudeLabel = new Label(parent, SWT.NONE);
		latitudeLabel.setText("Latitude range");
		latitudeFromLabel = new Label(parent, SWT.NONE);
		latitudeFromLabel.setText("from");
		latitudeFromText = new Text(parent, SWT.BORDER);
		latitudeFromNote = new Label(parent, SWT.NONE);
		latitudeFromNote.setText("\u00B0 (southern limit)");
		latitudeToLabel = new Label(parent, SWT.NONE);
		latitudeToLabel.setText("to");
		latitudeToText = new Text(parent, SWT.BORDER);
		latitudeToNote = new Label(parent, SWT.NONE);
		latitudeToNote.setText("\u00B0 (northern limit)");

		longitudeLabel = new Label(parent, SWT.NONE);
		longitudeLabel.setText("Longitude range");
		longitudeFromLabel = new Label(parent, SWT.NONE);
		longitudeFromLabel.setText("from");
		longitudeFromText = new Text(parent, SWT.BORDER);
		longitudeFromNote = new Label(parent, SWT.NONE);
		longitudeFromNote.setText("\u00B0 (western limit)");
		longitudeToLabel = new Label(parent, SWT.NONE);
		longitudeToLabel.setText("to");
		longitudeToText = new Text(parent, SWT.BORDER);
		longitudeToNote = new Label(parent, SWT.NONE);
		longitudeToNote.setText("\u00B0 (eastern limit)");

		minimumMagnitudeLabel = new Label(parent, SWT.NONE);
		minimumMagnitudeLabel.setText("Minimum magnitude");
		minimumMagnitudeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		//		minimumMagnitudeFromLabel = new Label(parent, SWT.NONE);
		minimumMagnitudeFromText = new Text(parent, SWT.BORDER);

		Composite restrictComposite = new Composite(parent, SWT.NONE);
		restrictComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		restrictButton = new Button(restrictComposite, SWT.CHECK);
		restrictLabel = new Label(restrictComposite, SWT.NONE);
		restrictLabel.setText("Restrict to events with moment tensors");
		//		GridDataFactory.swtDefaults().span(4, 1).applyTo(restrictComposite);
		restrictComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 4, 1));

		outputFormatLabel = new Label(parent, SWT.NONE);
		outputFormatLabel.setText("Output format");
		final Composite radioComposite = new Composite(parent, SWT.NONE);
		radioComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		radioComposite.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		for (final String mode : new String[] { "RSS", "KML" }) {
			final Button radio = new Button(radioComposite, SWT.RADIO);
			radio.setSelection("RSS".equals(mode));
			radio.setText(mode);
			//			radio.addSelectionListener(new ModeRadioSelectionListener(this, radio, mode));
			modeRadios.put(mode, radio);
		}
		resultsLabel = new Label(parent, SWT.NONE);
		resultsLabel.setText("Limit results to");
		resultsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
		resultsText = new Text(parent, SWT.BORDER);

		searchButton = new Button(parent, SWT.NULL);
		searchButton.setText("Search");
	}

}
