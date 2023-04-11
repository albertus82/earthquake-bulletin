package io.github.albertus82.eqbulletin.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import io.github.albertus82.eqbulletin.gui.SearchForm;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClearButtonSelectionListener extends SelectionAdapter {

	@NonNull
	private final SearchForm form;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		form.getPeriodFromDateTime().setSelection(null);
		form.getPeriodFromDateTime().notifyListeners(SWT.KeyUp, null);
		form.getPeriodToDateTime().setSelection(null);
		form.getPeriodToDateTime().notifyListeners(SWT.KeyUp, null);
		form.setLatitudeFrom(null);
		form.setLatitudeTo(null);
		form.setLongitudeFrom(null);
		form.setLongitudeTo(null);
		form.getMinimumMagnitudeText().setText("");
		form.getMinimumMagnitudeText().notifyListeners(SWT.KeyUp, null);
		form.getResultsText().setText("");
		form.getResultsText().notifyListeners(SWT.KeyUp, null);
		form.getRestrictButton().setSelection(false);
	}

}
