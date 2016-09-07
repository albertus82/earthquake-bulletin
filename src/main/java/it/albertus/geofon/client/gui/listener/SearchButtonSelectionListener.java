package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.job.SearchJob;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private final GeofonClientGui gui;

	public SearchButtonSelectionListener(final GeofonClientGui gui) {
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
