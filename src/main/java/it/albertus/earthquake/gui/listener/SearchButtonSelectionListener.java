package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.job.SearchJob;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public SearchButtonSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if ((gui.getSearchForm().getSearchJob() == null || gui.getSearchForm().getSearchJob().getState() == Job.NONE)) {
			gui.getSearchForm().setSearchJob(new SearchJob(gui));
			gui.getSearchForm().getSearchJob().schedule();
		}
	}

}
