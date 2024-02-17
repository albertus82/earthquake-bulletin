package io.github.albertus82.eqbulletin.gui.listener;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.albertus82.jface.Formatter;
import io.github.albertus82.jface.JFaceMessages;
import io.github.albertus82.jface.listener.IntegerVerifyListener;

/**
 * A simple input dialog for soliciting an input string from the user.
 * <p>
 * This concrete dialog class can be instantiated as is, or further subclassed
 * as required.
 * </p>
 */
public class ScaleInputDialog extends Dialog {

	private static final Logger log = LoggerFactory.getLogger(ScaleInputDialog.class);

	/**
	 * The title of the dialog.
	 */
	private String title;

	/**
	 * The message to display, or <code>null</code> if none.
	 */
	private String message;

	/**
	 * The input value; the empty string by default.
	 */
	private int value;

	private final int minimum;
	private final int maximum;
	private final int increment;
	private final int pageIncrement;

	/**
	 * Ok button widget.
	 */
	private Button okButton;

	private Scale scale;

	/**
	 * Input text widget.
	 */
	private Text text;

	/**
	 * Creates an input dialog with OK and Cancel buttons. Note that the dialog will
	 * have no visual representation (no widgets) until it is told to open.
	 * <p>
	 * Note that the <code>open</code> method blocks for input dialogs.
	 * </p>
	 *
	 * @param parentShell the parent shell, or <code>null</code> to create a
	 *        top-level shell
	 * @param dialogTitle the dialog title, or <code>null</code> if none
	 * @param dialogMessage the dialog message, or <code>null</code> if none
	 * @param initialValue the initial input value, or <code>null</code> if none
	 *        (equivalent to the empty string)
	 * @param validator an input validator, or <code>null</code> if none
	 */
	public ScaleInputDialog(final Shell parentShell, final String dialogTitle, final String dialogMessage, final int initialValue, final int minimum, final int maximum, final int increment, final int pageIncrement) {
		super(parentShell);
		this.title = dialogTitle;
		message = dialogMessage;
		value = initialValue;
		this.minimum = minimum;
		this.maximum = maximum;
		this.increment = increment;
		this.pageIncrement = pageIncrement;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			value = scale.getSelection();
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		// create OK and Cancel buttons by default
		okButton = createButton(parent, IDialogConstants.OK_ID, JFaceMessages.get("lbl.button.ok"), true);
		createButton(parent, IDialogConstants.CANCEL_ID, JFaceMessages.get("lbl.button.cancel"), false);
		//do this here because setting the text will set enablement on the ok
		// button
		scale.setFocus();
		scale.setSelection(value);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		// create composite
		final Composite composite = (Composite) super.createDialogArea(parent);
		if (composite.getLayout() instanceof GridLayout) {
			((GridLayout) composite.getLayout()).numColumns += 3;
		}
		// create message
		if (message != null) {
			Label label = new Label(composite, SWT.WRAP);
			label.setText(message);
			final GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.ENTRY_FIELD_WIDTH);
			data.horizontalSpan += 3;
			label.setLayoutData(data);
			label.setFont(parent.getFont());
		}
		scale = new Scale(composite, getScaleStyle());
		scale.setMinimum(minimum);
		scale.setMaximum(maximum);
		scale.setIncrement(increment);
		scale.setPageIncrement(pageIncrement);
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.verticalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		scale.setLayoutData(data);
		scale.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				text.setText(Integer.toString(scale.getSelection()));
			}
		});

		Label label = new Label(composite, SWT.NONE);
		label.setText("\u00B1");
		GridDataFactory.swtDefaults().applyTo(label);
		label.setFont(parent.getFont());

		text = new Text(composite, SWT.BORDER | SWT.TRAIL);
		final int widthHint = new Formatter(getClass()).computeWidth(text, Integer.toString(maximum).length(), SWT.NORMAL);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(widthHint, SWT.DEFAULT).applyTo(text);
		text.setTextLimit(Integer.toString(maximum).length());
		text.setText(Integer.toString(value));
		text.addFocusListener(new TextFocusListener());
		text.addVerifyListener(new IntegerVerifyListener(false));

		Label label2 = new Label(composite, SWT.NONE);
		label2.setText("\u00B0");
		GridDataFactory.swtDefaults().applyTo(label2);
		label2.setFont(parent.getFont());

		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Returns the ok button.
	 *
	 * @return the ok button
	 */
	protected Button getOkButton() {
		return okButton;
	}

	/**
	 * Returns the text area.
	 *
	 * @return the text area
	 */
	protected Scale getScale() {
		return scale;
	}

	/**
	 * Returns the string typed into this input dialog.
	 *
	 * @return the input string
	 */
	public Integer getValue() {
		return value;
	}

	/**
	 * Returns the style bits that should be used for the input text field. Defaults
	 * to a single line entry. Subclasses may override.
	 *
	 * @return the integer style bits that should be used when creating the input
	 *         text
	 *
	 * @since 3.4
	 */
	protected int getScaleStyle() {
		return SWT.HORIZONTAL;
	}

	protected class TextFocusListener extends FocusAdapter {
		@Override
		public void focusLost(final FocusEvent fe) {
			try {
				int textValue = Integer.parseInt(text.getText());
				if (textValue > maximum) {
					textValue = maximum;
				}
				if (textValue < minimum) {
					textValue = minimum;
				}
				text.setText(Integer.toString(textValue));
				scale.setSelection(textValue);
			}
			catch (final RuntimeException e) {
				log.debug("Cannot update the selection (which is the value) of the scale:", e);
				text.setText(Integer.toString(scale.getSelection()));
			}
		}
	}

}
