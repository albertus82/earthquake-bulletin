package it.albertus.earthquake.gui;

import java.text.DateFormat;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.SwtUtils;
import it.albertus.jface.listener.LinkSelectionListener;
import it.albertus.util.Version;

public class AboutDialog extends Dialog {

	private static final double MONITOR_SIZE_DIVISOR = 1.2;

	public AboutDialog(final Shell parent) {
		this(parent, SWT.SHEET);
	}

	public AboutDialog(final Shell parent, final int style) {
		super(parent, style);
		this.setText(Messages.get("lbl.about.title"));
	}

	public void open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		shell.setImage(shell.getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		createContents(shell);
		constrainShellSize(shell);
		shell.open();
	}

	private void createContents(final Shell shell) {
		GridLayoutFactory.swtDefaults().applyTo(shell);

		final LinkSelectionListener linkSelectionListener = new LinkSelectionListener();

		final Link info = new Link(shell, SWT.WRAP);
		final Version version = Version.getInstance();
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(info);
		info.setText(buildAnchor(Messages.get("url"), Messages.get("msg.application.name")) + ' ' + Messages.get("msg.version", version.getNumber(), DateFormat.getDateInstance(DateFormat.MEDIUM, Messages.getLanguage().getLocale()).format(version.getDate())));
		info.addSelectionListener(linkSelectionListener);

		final Link acknowledgementsLocations = new Link(shell, SWT.WRAP);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(acknowledgementsLocations);
		acknowledgementsLocations.setText(Messages.get("lbl.about.acknowledgements.locations", buildAnchor(Messages.get("url.geofon"), Messages.get("lbl.geofon")), buildAnchor(Messages.get("url.gfz"), Messages.get("lbl.gfz"))));
		acknowledgementsLocations.addSelectionListener(linkSelectionListener);

		final Label acknowledgementsData = new Label(shell, SWT.WRAP);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(acknowledgementsData);
		acknowledgementsData.setText(Messages.get("lbl.about.acknowledgements.data", Messages.get("lbl.geofon"), Messages.get("lbl.gfz")));

		final Link linkLicense = new Link(shell, SWT.WRAP);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(linkLicense);
		linkLicense.setText(Messages.get("lbl.about.license.thirdparty", buildAnchor(Messages.get("url.epl"), Messages.get("lbl.epl"))));
		linkLicense.addSelectionListener(linkSelectionListener);

		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(Messages.get("lbl.button.ok"));
		final int buttonWidth = SwtUtils.convertHorizontalDLUsToPixels(okButton, IDialogConstants.BUTTON_WIDTH);
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).minSize(buttonWidth, SWT.DEFAULT).applyTo(okButton);
		okButton.setFocus();
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent se) {
				shell.close();
			}
		});
		shell.setDefaultButton(okButton);
	}

	private void constrainShellSize(final Shell shell) {
		final int preferredWidth = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
		final int clientWidth = shell.getMonitor().getClientArea().width;
		if (preferredWidth > clientWidth / MONITOR_SIZE_DIVISOR) {
			final int wHint = (int) (clientWidth / MONITOR_SIZE_DIVISOR);
			shell.setSize(wHint, shell.computeSize(wHint, SWT.DEFAULT, true).y);
		}
		else {
			shell.pack();
		}
		shell.setMinimumSize(shell.getSize());
	}

	private static String buildAnchor(final String href, final String label) {
		return new StringBuilder("<a href=\"").append(href).append("\">").append(label).append("</a>").toString();
	}

	/*
	 * public static void main(String[] args) { Display display = new Display();
	 * Shell shell = new Shell(display); GridLayout layout = new GridLayout();
	 * shell.setLayout(layout); Label label = new Label(shell, SWT.WRAP); final
	 * GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
	 * label.setLayoutData(data); label.setText(
	 * "asda sda sdasd asda sda sdada dadads asd adsad as dadsa sad sada dsasda sd adsa sasd"
	 * ); List list = new List(shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	 * list.setLayoutData(new GridData(GridData.FILL_BOTH)); for (int i = 0; i <
	 * 100; i++) { list.add("asddas "+i); } shell.open(); while
	 * (!shell.isDisposed()) { if (!display.readAndDispatch()) display.sleep();
	 * } display.dispose(); }
	 */
}
