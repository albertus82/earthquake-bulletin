package it.albertus.eqbulletin.gui;

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

import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.SwtUtils;

public class MomentTensorDialog extends Dialog {

	private final String contents;

	private static final boolean LIMIT_HEIGHT = false;

	public MomentTensorDialog(final Shell parent, final String contents) {
		super(parent, SWT.SHEET | SWT.RESIZE | SWT.MAX);
		if (contents == null) {
			throw new NullPointerException("contents cannot be null");
		}
		this.contents = contents.trim();
		setText(Messages.get("lbl.mt.title"));
	}

	public void open() {
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

	private static Point getSize(final Shell shell) {
		final Point normalShellSize = shell.getSize();
		final int size = Math.min(normalShellSize.x, normalShellSize.y);
		return new Point(size, size);
	}

	private void createContents(final Shell shell) {
		final Text text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		text.setEditable(false);
		if (Util.isWindows()) {
			text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		}
		text.setFont(JFaceResources.getTextFont());
		text.setText(contents);
		GC gc = null;
		try {
			gc = new GC(text);
			final Point textExtent = gc.textExtent(contents);
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

}
