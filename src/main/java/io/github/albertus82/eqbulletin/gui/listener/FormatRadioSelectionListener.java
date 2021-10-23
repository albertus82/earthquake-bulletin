package io.github.albertus82.eqbulletin.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import io.github.albertus82.eqbulletin.gui.SearchForm;
import io.github.albertus82.eqbulletin.model.Format;
import it.albertus.jface.validation.ControlValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FormatRadioSelectionListener extends SelectionAdapter {

	@NonNull
	private final SearchForm form;
	@NonNull
	private final Button radio;
	@NonNull
	private final Format format;

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (radio.getSelection()) {
			if (Format.RSS.equals(format)) {
				form.getPeriodLabel().setEnabled(false);
				form.getPeriodFromLabel().setEnabled(false);
				form.getPeriodFromDateTime().setEnabled(false);
				form.getPeriodFromNote().setEnabled(false);
				form.getPeriodToLabel().setEnabled(false);
				form.getPeriodToDateTime().setEnabled(false);
				form.getPeriodToNote().setEnabled(false);
				form.getResultsLabel().setEnabled(false);
				form.getResultsText().setEnabled(false);
				for (final ControlValidator<Text> cv : form.getValidators()) {
					if (form.getResultsText().equals(cv.getControl()) && !cv.isValid()) {
						form.getResultsText().setText("");
						form.getResultsText().notifyListeners(SWT.KeyUp, null); // Clear error
					}
				}
			}
			else {
				form.getPeriodLabel().setEnabled(true);
				form.getPeriodFromLabel().setEnabled(true);
				form.getPeriodFromDateTime().setEnabled(true);
				form.getPeriodFromNote().setEnabled(true);
				form.getPeriodToLabel().setEnabled(true);
				form.getPeriodToDateTime().setEnabled(true);
				form.getPeriodToNote().setEnabled(true);
				form.getResultsLabel().setEnabled(true);
				form.getResultsText().setEnabled(true);
			}
		}
	}

}
