package it.albertus.earthquake.gui.listener;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.MessageBox;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.resources.Messages;

public class FormFieldTraverseListener implements TraverseListener {

	private final SearchForm form;

	public FormFieldTraverseListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void keyTraversed(final TraverseEvent te) {
		if (te.detail == SWT.TRAVERSE_RETURN) {
			if (form.getSearchButton().isEnabled()) {
				form.getSearchButton().notifyListeners(SWT.Selection, null);
			}
			else if (form.isValid() && form.getSearchJob() != null && form.getSearchJob().getState() != Job.NONE) {
				final MessageBox dialog = new MessageBox(form.getFormComposite().getShell(), SWT.ICON_WARNING);
				dialog.setText(Messages.get("lbl.window.title"));
				dialog.setMessage(Messages.get("msg.form.search.ongoing.message"));
				dialog.open();
			}
		}
	}

}
