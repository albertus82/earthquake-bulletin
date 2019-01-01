package it.albertus.eqbulletin.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.async.SearchJob;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public SearchButtonSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if (gui.getSearchForm().getSearchJob() == null || gui.getSearchForm().getSearchJob().getState() != Job.RUNNING) { // Submit
			if (gui.getSearchForm().isValid()) {
				gui.getSearchForm().cancelJob();
				gui.getSearchForm().setSearchJob(new SearchJob(gui));
				gui.getSearchForm().getSearchJob().schedule();
			}
		}
		else {
			gui.getSearchForm().cancelJob(); // Cancel
		}
	}

}
