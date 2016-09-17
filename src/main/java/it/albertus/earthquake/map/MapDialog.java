package it.albertus.earthquake.map;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.resources.Messages;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MapDialog extends Dialog {

	private final SearchForm form;
	private Browser browser;

	public MapDialog(final SearchForm form) {
		super(form.getFormComposite().getShell(), SWT.SHEET | SWT.RESIZE | SWT.WRAP | SWT.MAX);
		this.form = form;
	}

	public void open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
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

		browser = new Browser(shell, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);
		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				browser.execute("document.getElementById('map_canvas').style.width= " + (browser.getSize().x - 20) + ";");
				browser.execute("document.getElementById('map_canvas').style.height= " + (browser.getSize().y - 20) + ";");
			}
		});

		browser.setUrl(_MapDialog.class.getResource("map.html").toString());//f.toURI().toString());

		final Composite buttonComposite = new Composite(shell, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, false).applyTo(buttonComposite);

		final Button okButton = new Button(buttonComposite, SWT.PUSH);
		okButton.setText(Messages.get("lbl.button.ok"));
		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gridData.minimumWidth = 90;
		okButton.setLayoutData(gridData);
		okButton.setFocus();
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				System.out.println(browser.evaluate("return map.getBounds().getNorthEast().lat();"));
				System.out.println(browser.evaluate("return map.getBounds().getSouthWest().lat();"));
				System.out.println(browser.evaluate("return map.getBounds().getNorthEast().lng();"));
				System.out.println(browser.evaluate("return map.getBounds().getSouthWest().lng();"));
			}
		});

		final Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText(Messages.get("lbl.button.cancel"));
		gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		gridData.minimumWidth = 90;
		cancelButton.setLayoutData(gridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				shell.close();
			}
		});
		shell.setDefaultButton(okButton);
	}

}
