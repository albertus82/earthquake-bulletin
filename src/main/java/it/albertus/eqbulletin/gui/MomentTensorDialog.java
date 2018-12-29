package it.albertus.eqbulletin.gui;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.window.Window;
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
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import it.albertus.eqbulletin.resources.Messages;
import it.albertus.jface.JFaceMessages;
import it.albertus.jface.SwtUtils;

public class MomentTensorDialog extends Dialog {

	private final String momentTensorSolution;

	public MomentTensorDialog(final Shell parent, final String momentTensorSolution) {
		super(parent, SWT.SHEET | SWT.RESIZE | SWT.MAX);
		if (momentTensorSolution == null) {
			throw new NullPointerException("momentTensorSolution cannot be null");
		}
		this.momentTensorSolution = momentTensorSolution.trim();
		setText(Messages.get("lbl.mt.title"));
	}

	public Composite createButtonBox(final Shell shell) {
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

	public int open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		final Image[] images = Images.getMainIconArray();
		if (images != null && images.length > 0) {
			shell.setImages(images);
		}
		createContents(shell);
		final Point minimumSize = getMinimumSize(shell);
		shell.setMinimumSize(minimumSize);
		shell.pack();
		shell.open();
		final Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return Window.CANCEL;
	}

	protected Point getSize(final Shell shell) {
		final Point normalShellSize = shell.getSize();
		final int size = Math.min(normalShellSize.x, normalShellSize.y);
		return new Point(size, size);
	}

	protected Point getMinimumSize(final Shell shell) {
		return shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
	}

	protected Layout getLayout() {
		return GridLayoutFactory.swtDefaults().create();
	}

	protected void createContents(final Shell shell) {
		shell.setLayout(getLayout());
		final Text text = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		if (Util.isWindows()) {
			text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		}
		text.setFont(JFaceResources.getTextFont());
		text.setText(momentTensorSolution);
		final Point defaultSize = getSize(shell);
		GC gc = null;
		try {
			gc = new GC(text);
			final Point textExtent = gc.textExtent(momentTensorSolution);
			GridDataFactory.fillDefaults().hint(Math.min(textExtent.x, defaultSize.x), Math.min(textExtent.y, defaultSize.y)).applyTo(text);
		}
		finally {
			if (gc != null) {
				gc.dispose();
			}
		}
		createButtonBox(shell);
	}

}
