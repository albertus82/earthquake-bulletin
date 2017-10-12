package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.eqbulletin.gui.SearchForm;

public class AutoRefreshButtonSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public AutoRefreshButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final boolean checked = form.getAutoRefreshButton().getSelection();
		form.getAutoRefreshText().setEnabled(checked);
		if (!checked) {
			form.getStopButton().notifyListeners(SWT.Selection, null);
		}
	}

}
