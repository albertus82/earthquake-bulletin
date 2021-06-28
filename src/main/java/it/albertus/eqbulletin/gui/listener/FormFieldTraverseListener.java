package it.albertus.eqbulletin.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.async.SearchAsyncOperation;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FormFieldTraverseListener implements TraverseListener {

	@NonNull
	private final EarthquakeBulletinGui gui;

	@Override
	public void keyTraversed(@NonNull final TraverseEvent e) {
		final Job currentJob = SearchAsyncOperation.getCurrentJob();
		if (e.detail == SWT.TRAVERSE_RETURN && (currentJob == null || currentJob.getState() != Job.RUNNING)) {
			SearchAsyncOperation.execute(gui);
		}
	}

}
