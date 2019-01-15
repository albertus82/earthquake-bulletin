package it.albertus.eqbulletin.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.async.SearchAsyncOperation;

public class FormFieldTraverseListener implements TraverseListener {

	private final EarthquakeBulletinGui gui;

	public FormFieldTraverseListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void keyTraversed(final TraverseEvent e) {
		final Job currentJob = SearchAsyncOperation.getCurrentJob();
		if (e.detail == SWT.TRAVERSE_RETURN && (currentJob == null || currentJob.getState() != Job.RUNNING)) {
			SearchAsyncOperation.execute(gui);
		}
	}

}
