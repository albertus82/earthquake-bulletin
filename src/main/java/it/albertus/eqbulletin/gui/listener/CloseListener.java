package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import it.albertus.eqbulletin.gui.CloseDialog;
import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;

public class CloseListener implements Listener, SelectionListener {

	private final EarthquakeBulletinGui gui;

	public CloseListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	private boolean canClose() {
		return !CloseDialog.mustShow() || CloseDialog.open(gui.getShell()) == SWT.YES;
	}

	private void disposeShellAndDisplay() {
		final Shell shell = gui.getShell();
		if (shell != null && !shell.isDisposed()) {
			gui.saveShellStatus();
			shell.dispose();
		}
		final Display display = Display.getCurrent();
		if (display != null && !display.isDisposed()) {
			display.dispose(); // fixes close not working on Windows 10 when iconified
		}
	}

	// Shell close command & macOS menu
	@Override
	public void handleEvent(final Event event) {
		if (canClose()) {
			disposeShellAndDisplay();
		}
		else if (event != null) {
			event.doit = false;
		}
	}

	// Menu
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (canClose()) {
			disposeShellAndDisplay();
		}
		else if (event != null) {
			event.doit = false;
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {/* Ignore */}

}
