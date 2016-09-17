package it.albertus.earthquake.map;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class MapDialog {

	static Browser browser;

	public static void main(String[] args) throws IOException {
		Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		SashForm sash = new SashForm(shell, SWT.VERTICAL);

		try {
			browser = new Browser(sash, SWT.NONE);
			browser.addControlListener(new ControlAdapter() {
				@Override
				public void controlResized(ControlEvent e) {
					browser.execute("document.getElementById('map_canvas').style.width= " + (browser.getSize().x - 20) + ";");
					browser.execute("document.getElementById('map_canvas').style.height= " + (browser.getSize().y - 20) + ";");
				}
			});
		}
		catch (SWTError e) {
			System.out.println("Could not instantiate Browser: " + e.getMessage());
			display.dispose();
			return;
		}

		Composite c = new Composite(sash, SWT.BORDER);
		c.setLayout(new GridLayout(1, true));
		Button b = new Button(c, SWT.PUSH);
		b.setText("Where Am I ?");
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) { // Can explode!
				System.out.println(browser.evaluate("return map.getBounds().getNorthEast().lat();"));
				System.out.println(browser.evaluate("return map.getBounds().getSouthWest().lat();"));
				System.out.println(browser.evaluate("return map.getBounds().getNorthEast().lng();"));
				System.out.println(browser.evaluate("return map.getBounds().getSouthWest().lng();"));
			}
		});

		browser.setUrl(MapDialog.class.getResource("map.html").toString());//f.toURI().toString());
		sash.setWeights(new int[] { 4, 1 });
		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

}
