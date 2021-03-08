package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.eqbulletin.gui.async.SearchAsyncOperation;
import it.albertus.eqbulletin.resources.Messages;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutoRefreshButtonSelectionListener extends SelectionAdapter {

	private final @NonNull SearchForm form;

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
