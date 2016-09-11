package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.SearchForm;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

public class FormTextTraverseListener implements TraverseListener {

	private final SearchForm form;

	public FormTextTraverseListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void keyTraversed(final TraverseEvent te) {
		if (te.detail == SWT.TRAVERSE_RETURN) {
			form.getSearchButton().notifyListeners(SWT.Selection, null);
		}
	}

}