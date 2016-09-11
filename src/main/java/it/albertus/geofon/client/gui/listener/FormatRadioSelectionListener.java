package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.SearchForm;
import it.albertus.geofon.client.model.Format;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class FormatRadioSelectionListener extends SelectionAdapter {

	private final SearchForm form;
	private final Format format;
	private final Button radio;

	public FormatRadioSelectionListener(final SearchForm form, final Button radio, final Format format) {
		this.form = form;
		this.radio = radio;
		this.format = format;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if (radio.getSelection()) {
			if (Format.RSS.equals(format)) {
				form.getPeriodFromText().setEnabled(false);
				form.getPeriodToText().setEnabled(false);
				form.getResultsText().setEnabled(false);
				form.getPeriodFromLabel().setEnabled(false);
				form.getPeriodFromNote().setEnabled(false);
				form.getPeriodToLabel().setEnabled(false);
				form.getPeriodToNote().setEnabled(false);
				form.getResultsLabel().setEnabled(false);
				form.getPeriodLabel().setEnabled(false);
				form.getResultsText().setText("20");
			}
			else {
				form.getPeriodFromText().setEnabled(true);
				form.getPeriodToText().setEnabled(true);
				form.getResultsText().setEnabled(true);
				form.getPeriodFromLabel().setEnabled(true);
				form.getPeriodFromNote().setEnabled(true);
				form.getPeriodToLabel().setEnabled(true);
				form.getPeriodToNote().setEnabled(true);
				form.getResultsLabel().setEnabled(true);
				form.getPeriodLabel().setEnabled(true);
			}
		}
	}

}
