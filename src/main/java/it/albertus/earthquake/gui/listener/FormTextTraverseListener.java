package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.resources.Messages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.MessageBox;

public class FormTextTraverseListener implements TraverseListener {

	private final SearchForm form;

	public FormTextTraverseListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void keyTraversed(final TraverseEvent te) {
		if (te.detail == SWT.TRAVERSE_RETURN) {
			if (form.getSearchButton().isEnabled()) {
				form.getSearchButton().notifyListeners(SWT.Selection, null);
			}
			else {
				final MessageBox dialog = new MessageBox(form.getFormComposite().getShell(), SWT.ICON_INFORMATION);
				dialog.setText(Messages.get("lbl.window.title"));
				dialog.setMessage(Messages.get("msg.form.search.ongoing.message"));
				dialog.open();
			}
		}
	}

}