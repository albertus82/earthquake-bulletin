package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;

import it.albertus.eqbulletin.gui.SearchForm;

public class FormFieldTraverseListener implements TraverseListener {

	private final SearchForm form;

	public FormFieldTraverseListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void keyTraversed(final TraverseEvent e) {
		if (e.detail == SWT.TRAVERSE_RETURN) {
			form.getSearchButton().notifyListeners(SWT.Selection, null);
		}
	}

}
