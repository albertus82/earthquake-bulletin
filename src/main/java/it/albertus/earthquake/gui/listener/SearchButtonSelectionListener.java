package it.albertus.earthquake.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.gui.job.SearchJob;
import it.albertus.earthquake.service.BulletinProvider;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;
	private final BulletinProvider provider;

	public SearchButtonSelectionListener(final EarthquakeBulletinGui gui, final BulletinProvider provider) {
		this.gui = gui;
		this.provider = provider;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if (gui.getSearchForm().isValid() && (gui.getSearchForm().getSearchJob() == null || gui.getSearchForm().getSearchJob().getState() == Job.NONE)) {
			gui.getSearchForm().setSearchJob(new SearchJob(gui, provider));
			gui.getSearchForm().getSearchJob().schedule();
		}
	}

}
