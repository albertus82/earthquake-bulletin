package com.github.albertus82.eqbulletin.gui.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import com.github.albertus82.eqbulletin.gui.SearchForm;
import com.github.albertus82.eqbulletin.gui.async.SearchAsyncOperation;
import com.github.albertus82.eqbulletin.resources.Messages;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoRefreshButtonSelectionListener extends SelectionAdapter {

	@NonNull
	private final SearchForm form;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final boolean checked = form.getAutoRefreshButton().getSelection();
		form.getAutoRefreshText().setEnabled(checked);
		if (!checked) {
			SearchAsyncOperation.cancelCurrentJob();
			final Button searchButton = form.getSearchButton();
			searchButton.setText(Messages.get("label.form.button.submit"));
			searchButton.setEnabled(true);
		}
	}

}
