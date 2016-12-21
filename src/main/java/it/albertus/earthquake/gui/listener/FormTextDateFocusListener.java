package it.albertus.earthquake.gui.listener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Text;

public class FormTextDateFocusListener extends FocusAdapter {

	private final DateFormat dateFormat;

	public FormTextDateFocusListener(final String dateFormatPattern) {
		dateFormat = new SimpleDateFormat(dateFormatPattern);
		dateFormat.setLenient(false);
	}

	@Override
	public void focusLost(final FocusEvent fe) {
		final Text text = (Text) fe.widget;
		final String oldText = text.getText();
		try {
			final String newText = dateFormat.format(dateFormat.parse(text.getText()));
			if (!oldText.equals(newText)) {
				text.setText(newText);
			}
		}
		catch (final ParseException pe) {/* Ignore */}
	}

}
