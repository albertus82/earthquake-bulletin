package it.albertus.earthquake.map;

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

	private Double northEastLat;
	private Double southWestLat;
	private Double northEastLng;
	private Double southWestLng;

	private volatile int returnCode = SWT.CANCEL;

	private Browser browser;

	public MapDialog(final Shell shell) {
		super(shell, SWT.SHEET | SWT.RESIZE | SWT.WRAP | SWT.MAX);
	}

	public int open() {
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
		return returnCode;
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(1, false));

		browser = new Browser(shell, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);
		browser.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent ce) {
				try {
					browser.execute("document.getElementById('map_canvas').style.width= " + (browser.getSize().x - 20) + ";");
					browser.execute("document.getElementById('map_canvas').style.height= " + (browser.getSize().y - 20) + ";");
				}
				catch (Exception e) {
					e.printStackTrace();
				}
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
			public void widgetSelected(final SelectionEvent se) {
				try {
					northEastLat = (Double) browser.evaluate("return map.getBounds().getNorthEast().lat();");
					southWestLat = (Double) browser.evaluate("return map.getBounds().getSouthWest().lat();");
					northEastLng = (Double) browser.evaluate("return map.getBounds().getNorthEast().lng();");
					southWestLng = (Double) browser.evaluate("return map.getBounds().getSouthWest().lng();");
					returnCode = SWT.OK;
					shell.close();
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
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

	public Double getNorthEastLat() {
		return northEastLat;
	}

	public Double getSouthWestLat() {
		return southWestLat;
	}

	public Double getNorthEastLng() {
		return northEastLng;
	}

	public Double getSouthWestLng() {
		return southWestLng;
	}

}
