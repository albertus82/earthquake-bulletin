package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.SearchForm;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ClearButtonSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public ClearButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		form.getLatitudeFromText().setText("");
		form.getLatitudeToText().setText("");
		form.getLongitudeFromText().setText("");
		form.getLongitudeToText().setText("");
		form.getMinimumMagnitudeText().setText("");
		form.getPeriodFromText().setText("");
		form.getPeriodToText().setText("");
		form.getRestrictButton().setSelection(false);
	}

}
