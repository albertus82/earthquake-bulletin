package it.albertus.geofon.client.gui;

import it.albertus.geofon.client.gui.job.SearchJob;
import it.albertus.geofon.client.gui.listener.AutoRefreshButtonSelectionListener;
import it.albertus.geofon.client.gui.listener.ClearButtonSelectionListener;
import it.albertus.geofon.client.gui.listener.FormatRadioSelectionListener;
import it.albertus.geofon.client.gui.listener.SearchButtonSelectionListener;
import it.albertus.geofon.client.gui.listener.StopButtonSelectionListener;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SearchForm {

	private final Composite formComposite;

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
	private final Composite radioComposite;
	private final Map<String, Button> formatRadios = new LinkedHashMap<String, Button>();
	private final Label resultsLabel;
	private final Text resultsText;

	private final Label separator;

	private final Composite buttonsComposite;
	private final Button searchButton;
	private final Label resultsNote;
	private final Button autoRefreshButton;
	private final Text autoRefreshText;
	private final Button stopButton;
	private final Button clearButton;

	private SearchJob searchJob;

	public SearchForm(final GeofonClientGui gui) {
		formComposite = new Composite(gui.getShell(), SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(9).applyTo(formComposite);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(formComposite);

		periodLabel = new Label(formComposite, SWT.NONE);
		periodLabel.setText("Time Period");
		periodFromLabel = new Label(formComposite, SWT.NONE);
		periodFromLabel.setText("from");
		periodFromText = new Text(formComposite, SWT.BORDER);
		periodFromText.setTextLimit(10);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(periodFromText);
		periodFromNote = new Label(formComposite, SWT.NONE);
		periodFromNote.setText("(yyyy-mm-dd)");
		periodToLabel = new Label(formComposite, SWT.NONE);
		periodToLabel.setText("to");
		periodToText = new Text(formComposite, SWT.BORDER);
		periodToText.setTextLimit(10);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(periodToText);
		periodToNote = new Label(formComposite, SWT.NONE);
		periodToNote.setText("(yyyy-mm-dd)");

		separator = new Label(formComposite, SWT.SEPARATOR | SWT.VERTICAL);
		GridDataFactory.fillDefaults().span(1, 5).applyTo(separator);

		buttonsComposite = new Composite(formComposite, SWT.NONE);
		GridLayoutFactory.swtDefaults().margins(0, 0).numColumns(1).applyTo(buttonsComposite);
		GridDataFactory.fillDefaults().grab(true, true).span(1, 5).applyTo(buttonsComposite);

		latitudeLabel = new Label(formComposite, SWT.NONE);
		latitudeLabel.setText("Latitude range");
		latitudeFromLabel = new Label(formComposite, SWT.NONE);
		latitudeFromLabel.setText("from");
		latitudeFromText = new Text(formComposite, SWT.BORDER);
		latitudeFromText.setTextLimit(7);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeFromText);
		latitudeFromNote = new Label(formComposite, SWT.NONE);
		latitudeFromNote.setText("\u00B0 (southern limit)");
		latitudeToLabel = new Label(formComposite, SWT.NONE);
		latitudeToLabel.setText("to");
		latitudeToText = new Text(formComposite, SWT.BORDER);
		latitudeToText.setTextLimit(7);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(latitudeToText);
		latitudeToNote = new Label(formComposite, SWT.NONE);
		latitudeToNote.setText("\u00B0 (northern limit)");

		longitudeLabel = new Label(formComposite, SWT.NONE);
		longitudeLabel.setText("Longitude range");
		longitudeFromLabel = new Label(formComposite, SWT.NONE);
		longitudeFromLabel.setText("from");
		longitudeFromText = new Text(formComposite, SWT.BORDER);
		longitudeFromText.setTextLimit(7);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeFromText);
		longitudeFromNote = new Label(formComposite, SWT.NONE);
		longitudeFromNote.setText("\u00B0 (western limit)");
		longitudeToLabel = new Label(formComposite, SWT.NONE);
		longitudeToLabel.setText("to");
		longitudeToText = new Text(formComposite, SWT.BORDER);
		longitudeToText.setTextLimit(7);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(longitudeToText);
		longitudeToNote = new Label(formComposite, SWT.NONE);
		longitudeToNote.setText("\u00B0 (eastern limit)");

		minimumMagnitudeLabel = new Label(formComposite, SWT.NONE);
		minimumMagnitudeLabel.setText("Minimum magnitude");
		GridDataFactory.swtDefaults().span(2, 1).applyTo(minimumMagnitudeLabel);
		minimumMagnitudeText = new Text(formComposite, SWT.BORDER);
		minimumMagnitudeText.setTextLimit(3);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(minimumMagnitudeText);
		restrictButton = new Button(formComposite, SWT.CHECK);
		restrictButton.setText("Restrict to events with moment tensors");
		GridDataFactory.swtDefaults().span(4, 1).applyTo(restrictButton);

		outputFormatLabel = new Label(formComposite, SWT.NONE);
		outputFormatLabel.setText("Output format");
		radioComposite = new Composite(formComposite, SWT.NONE);
		radioComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		GridDataFactory.swtDefaults().grab(false, false).span(2, 1).applyTo(radioComposite);
		for (final String mode : new String[] { "RSS", "KML" }) {
			final Button radio = new Button(radioComposite, SWT.RADIO);
			radio.addSelectionListener(new FormatRadioSelectionListener(this, radio, mode));
			radio.setText(mode);
			radio.setSelection("RSS".equalsIgnoreCase(mode));
			radio.setEnabled("RSS".equalsIgnoreCase(mode));
			formatRadios.put(mode, radio);
		}
		resultsLabel = new Label(formComposite, SWT.NONE);
		resultsLabel.setText("Limit results to");
		GridDataFactory.swtDefaults().grab(false, false).span(2, 1).applyTo(resultsLabel);
		resultsText = new Text(formComposite, SWT.BORDER);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(resultsText);
		resultsText.setTextLimit(3);

		resultsNote = new Label(formComposite, SWT.NONE);
		resultsNote.setText("events");

		// Buttons
		autoRefreshButton = new Button(buttonsComposite, SWT.CHECK);
		autoRefreshButton.setText("Auto refresh every (secs)");
		GridDataFactory.swtDefaults().applyTo(autoRefreshButton);

		autoRefreshText = new Text(buttonsComposite, SWT.BORDER);
		autoRefreshText.setTextLimit(10);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(autoRefreshText);

		searchButton = new Button(buttonsComposite, SWT.NONE);
		searchButton.setText("Submit");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(searchButton);

		stopButton = new Button(buttonsComposite, SWT.NONE);
		stopButton.setText("Stop");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(stopButton);
		stopButton.setEnabled(false);

		clearButton = new Button(buttonsComposite, SWT.NONE);
		clearButton.setText("Clear");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(clearButton);

		// Listeners
		searchButton.addSelectionListener(new SearchButtonSelectionListener(gui));
		stopButton.addSelectionListener(new StopButtonSelectionListener(this));
		clearButton.addSelectionListener(new ClearButtonSelectionListener(this));
		autoRefreshButton.addSelectionListener(new AutoRefreshButtonSelectionListener(this));
		autoRefreshButton.notifyListeners(SWT.Selection, null);
	}

	public SearchJob getSearchJob() {
		return searchJob;
	}

	public void setSearchJob(SearchJob searchJob) {
		this.searchJob = searchJob;
	}

	public Composite getFormComposite() {
		return formComposite;
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

	public Composite getRadioComposite() {
		return radioComposite;
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

	public Label getSeparator() {
		return separator;
	}

	public Composite getButtonsComposite() {
		return buttonsComposite;
	}

	public Button getSearchButton() {
		return searchButton;
	}

	public Label getResultsNote() {
		return resultsNote;
	}

	public Button getAutoRefreshButton() {
		return autoRefreshButton;
	}

	public Text getAutoRefreshText() {
		return autoRefreshText;
	}

	public Button getStopButton() {
		return stopButton;
	}

	public Button getClearButton() {
		return clearButton;
	}

}
