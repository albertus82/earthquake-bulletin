package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.SearchForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class ClearButtonSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public ClearButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		form.getPeriodFromText().setText("");
		form.getPeriodFromText().notifyListeners(SWT.KeyUp, null);
		form.getPeriodToText().setText("");
		form.getPeriodToText().notifyListeners(SWT.KeyUp, null);
		form.getLatitudeFromText().setText("");
		form.getLatitudeFromText().notifyListeners(SWT.KeyUp, null);
		form.getLatitudeToText().setText("");
		form.getLatitudeToText().notifyListeners(SWT.KeyUp, null);
		form.getLongitudeFromText().setText("");
		form.getLongitudeFromText().notifyListeners(SWT.KeyUp, null);
		form.getLongitudeToText().setText("");
		form.getLongitudeToText().notifyListeners(SWT.KeyUp, null);
		form.getMinimumMagnitudeText().setText("");
		form.getMinimumMagnitudeText().notifyListeners(SWT.KeyUp, null);
		form.getResultsText().setText("");
		form.getResultsText().notifyListeners(SWT.KeyUp, null);
		form.getRestrictButton().setSelection(false);
	}

}
