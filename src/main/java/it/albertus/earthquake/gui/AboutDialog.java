package it.albertus.earthquake.gui;

import it.albertus.earthquake.resources.Messages;
import it.albertus.jface.listener.LinkSelectionListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {

	private String message = "";
	private String applicationUrl = "";
	private String iconUrl = "";

	public AboutDialog(final Shell parent) {
		this(parent, SWT.SHEET | SWT.RESIZE);
	}

	public AboutDialog(final Shell parent, final int style) {
		super(parent, style);
	}

	public void open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		shell.setImage(shell.getDisplay().getSystemImage(SWT.ICON_INFORMATION));
		createContents(shell);
		shell.pack();
		shell.open();
		final Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(1, false));

		GridData gridData;

		final Label info = new Label(shell, SWT.WRAP);
		info.setText(this.message);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		info.setLayoutData(gridData);

		final Link linkProject = new Link(shell, SWT.WRAP);
		linkProject.setText("<a href=\"" + getApplicationUrl() + "\">" + getApplicationUrl() + "</a>");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		linkProject.setLayoutData(gridData);
		linkProject.addSelectionListener(new LinkSelectionListener());

		final Label acknowledgementsLocations = new Label(shell, SWT.WRAP);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		acknowledgementsLocations.setLayoutData(gridData);
		acknowledgementsLocations.setText(Messages.get("lbl.about.acknowledgements.locations"));

		final Label acknowledgementsData = new Label(shell, SWT.WRAP);
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		acknowledgementsData.setLayoutData(gridData);
		acknowledgementsData.setText(Messages.get("lbl.about.acknowledgements.data"));

		final Link linkSource = new Link(shell, SWT.WRAP);
		linkSource.setText("<a href=\"http://geofon.gfz-potsdam.de\">http://geofon.gfz-potsdam.de</a>");
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		linkSource.setLayoutData(gridData);
		linkSource.addSelectionListener(new LinkSelectionListener());

		final Button okButton = new Button(shell, SWT.PUSH);
		okButton.setText(Messages.get("lbl.button.ok"));
		gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		gridData.minimumWidth = 90;
		okButton.setLayoutData(gridData);
		okButton.setFocus();
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent se) {
				shell.close();
			}
		});
		shell.setDefaultButton(okButton);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getApplicationUrl() {
		return applicationUrl;
	}

	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
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
