package it.albertus.eqbulletin.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.async.SearchJob;
import it.albertus.eqbulletin.resources.Messages;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public SearchButtonSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if (SearchJob.getCurrentJob() == null || SearchJob.getCurrentJob().getState() != Job.RUNNING) { // Submit
			if (gui.getSearchForm().isValid()) {
				SearchJob.scheduleNewJob(gui);
			}
		}
		else {
			SearchJob.cancelCurrentJob(); // Cancel
			gui.getSearchForm().getSearchButton().setText(Messages.get("lbl.form.button.submit"));
		}
	}

}
