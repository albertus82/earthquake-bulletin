package it.albertus.earthquake.gui.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.gui.job.SearchJob;

public class StopButtonSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public StopButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		form.getStopButton().setEnabled(false);
		final SearchJob job = form.getSearchJob();
		if (job != null) {
			job.setShouldRun(false);
			job.setShouldSchedule(false);
			job.cancel();
			form.setSearchJob(null);
			form.updateButtons();
		}
	}

}
