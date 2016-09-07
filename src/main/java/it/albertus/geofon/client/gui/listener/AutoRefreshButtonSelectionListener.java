package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.SearchForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

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
