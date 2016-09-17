package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.SearchForm;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class StopButtonSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public StopButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		form.getStopButton().setEnabled(false);
		if (form.getSearchJob() != null) {
			form.getSearchJob().setShouldRun(false);
			form.getSearchJob().setShouldSchedule(false);
			form.setSearchJob(null);
			form.getSearchButton().setEnabled(true);
		}
	}

}
