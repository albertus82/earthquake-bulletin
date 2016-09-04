package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.SearchJob;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private final GeofonClientGui gui;

	public SearchButtonSelectionListener(final GeofonClientGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if ((gui.getJob() == null || gui.getJob().getState() == Job.NONE)) {
			/* Disabilitazione controlli durante la ricerca */
			// gui.disableControls();

			/* Impostazione puntatore del mouse "Occupato" */
			gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));

			/* Parametri di ricerca */
			// final List<String> filters = new ArrayList<String>();

			/* Avvio della ricerca */
			gui.setJob(new SearchJob(gui));
			gui.getJob().schedule();
		}
	}

}
