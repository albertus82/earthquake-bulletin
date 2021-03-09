package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.VerifyEvent;

import it.albertus.jface.listener.TrimVerifyListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FormTextDateVerifyListener extends TrimVerifyListener {

	@NonNull private final String allowedChars;

	@Override
	public void verifyText(final VerifyEvent ve) {
		super.verifyText(ve);
		if (ve.text.length() > 0 && !containsOnlyAllowedChars(ve.text)) {
			ve.doit = false;
		}
	}

	private boolean containsOnlyAllowedChars(final String text) {
		for (int i = 0; i < text.length(); i++) {
			if (allowedChars.indexOf(text.charAt(i)) == -1) {
				return false;
			}
		}
		return true;
	}

}
