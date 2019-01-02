package it.albertus.eqbulletin.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.SwtUtils;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorDialog extends Dialog {

	private static final boolean LIMIT_HEIGHT = false;

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorDialog.class);

	private static final Collection<MomentTensorDialog> instances = new ArrayList<>();

	private MomentTensor momentTensor;
	private final Earthquake earthquake;

	private Text text;

	public MomentTensorDialog(final Shell parent, final MomentTensor momentTensor, final Earthquake earthquake) {
		super(parent, SWT.SHEET | SWT.RESIZE);
		this.earthquake = earthquake;
		this.momentTensor = momentTensor;
		addInstance(this); // Available for update on-the-fly.
		setText(Messages.get("lbl.mt.title"));
	}

	public void open() {
		try {
			final Shell shell = new Shell(getParent(), getStyle());
			final Point defaultSize = getSize(shell);
			shell.setText(getText());
			final Image[] images = Images.getMainIconArray();
			if (images != null && images.length > 0) {
				shell.setImages(images);
			}
			GridLayoutFactory.swtDefaults().applyTo(shell);
			createContents(shell);
			shell.pack();
			if (LIMIT_HEIGHT) {
				shell.setSize(shell.getSize().x, defaultSize.y);
			}
			shell.open();
			final Display display = getParent().getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		}
		finally {
			removeInstance(this); // Not available for update on-the-fly.
		}
	}

	private static Point getSize(final Shell shell) {
		final Point normalShellSize = shell.getSize();
		final int size = Math.min(normalShellSize.x, normalShellSize.y);
		return new Point(size, size);
	}

	private void createContents(final Shell shell) {
		text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setEditable(false);
		if (Util.isWindows()) {
			text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		}
		text.setFont(JFaceResources.getTextFont());
		final String momentTensorText = momentTensor.getText().trim();
		text.setText(momentTensorText);
		GC gc = null;
		try {
			gc = new GC(text);
			final Point textExtent = gc.textExtent(momentTensorText);
			GridDataFactory.fillDefaults().grab(true, true).hint(textExtent.x, SWT.DEFAULT).applyTo(text);
		}
		finally {
			if (gc != null) {
				gc.dispose();
			}
		}
		createButtonBox(shell);
	}

	private static Composite createButtonBox(final Shell shell) {
		final Composite buttonComposite = new Composite(shell, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(buttonComposite);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(buttonComposite);

		final Button closeButton = new Button(buttonComposite, SWT.PUSH);
		closeButton.setText(JFaceMessages.get("lbl.button.close"));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.FILL).grab(true, false).minSize(SwtUtils.convertHorizontalDLUsToPixels(closeButton, IDialogConstants.BUTTON_WIDTH), SWT.DEFAULT).applyTo(closeButton);
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent se) {
				shell.close();
			}
		});

		shell.setDefaultButton(closeButton);
		return buttonComposite;
	}

	private static synchronized void addInstance(final MomentTensorDialog instance) {
		instances.add(instance);
		logger.log(instances.size() == 1 ? Level.FINE : Level.WARNING, "Moment tensor dialog instance added; instances.size() = {0}.", instances.size());
	}

	private static synchronized void removeInstance(final MomentTensorDialog instance) {
		instances.remove(instance);
		logger.log(instances.isEmpty() ? Level.FINE : Level.WARNING, "Moment tensor dialog instance removed; instances.size() = {0}.", instances.size());
	}

	public static synchronized void updateMomentTensorText(final MomentTensor momentTensor, final Earthquake earthquake) {
		for (final MomentTensorDialog instance : instances) {
			if (earthquake.equals(instance.earthquake)) {
				logger.log(Level.FINE, "Updating moment tensor dialog instance {0}...", instance);
				instance.momentTensor = momentTensor; // Useful when the Text field is not initialized.
				if (instance.text != null && !instance.text.isDisposed()) {
					final String oldValue = instance.text.getText();
					final String newValue = instance.momentTensor.getText().trim();
					if (!newValue.equals(oldValue)) {
						instance.text.setText(newValue); // Update the Text field on-the-fly.
					}
				}
				logger.log(Level.FINE, "Moment tensor dialog instance {0} updated.", instance);
				return;
			}
		}
		logger.log(Level.WARNING, "No moment tensor dialog instance found to update for {0}.", earthquake);
	}

}
