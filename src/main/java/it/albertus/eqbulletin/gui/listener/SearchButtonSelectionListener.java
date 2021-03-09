package it.albertus.eqbulletin.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.async.SearchAsyncOperation;
import it.albertus.eqbulletin.resources.Messages;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SearchButtonSelectionListener extends SelectionAdapter {

	@NonNull private final EarthquakeBulletinGui gui;

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Job currentJob = SearchAsyncOperation.getCurrentJob();
		if (currentJob == null || currentJob.getState() != Job.RUNNING) { // Submit
			SearchAsyncOperation.execute(gui);
		}
		else {
			SearchAsyncOperation.cancelCurrentJob(); // Cancel
			final Button searchButton = gui.getSearchForm().getSearchButton();
			searchButton.setText(Messages.get("label.form.button.submit"));
			searchButton.setEnabled(true);
		}
	}

}
