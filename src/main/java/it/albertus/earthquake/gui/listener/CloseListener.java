package it.albertus.earthquake.gui.listener;

import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_LOCATION_X;
import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_LOCATION_Y;
import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_MAXIMIZED;
import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_SASH_WEIGHT;
import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_SIZE_X;
import static it.albertus.earthquake.gui.EarthquakeBulletinGui.SHELL_SIZE_Y;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.CloseDialog;
import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;

public class CloseListener implements Listener, SelectionListener {

	private static final Logger logger = LoggerFactory.getLogger(CloseListener.class);

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

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
			saveShellStatus(gui);
			shell.dispose();
		}
		final Display display = Display.getCurrent();
		if (display != null) {
			display.dispose(); // fix close not working on Windows 10 when iconified
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

	private static void saveShellStatus(final EarthquakeBulletinGui gui) {
		final Shell shell = gui.getShell();
		if (shell.getSize() != null && shell.getLocation() != null && configuration != null) {
			final Properties properties = configuration.getProperties();

			final boolean maximized = gui.isMaximized();
			properties.setProperty(SHELL_MAXIMIZED, Boolean.toString(maximized));
			if (maximized) { // if maximized, discard the other values
				properties.remove(SHELL_SIZE_X);
				properties.remove(SHELL_SIZE_Y);
				properties.remove(SHELL_LOCATION_X);
				properties.remove(SHELL_LOCATION_Y);
			}
			else { // if not maximized, save window size & location
				properties.setProperty(SHELL_SIZE_X, Integer.toString(shell.getSize().x));
				properties.setProperty(SHELL_SIZE_Y, Integer.toString(shell.getSize().y));
				properties.setProperty(SHELL_LOCATION_X, Integer.toString(shell.getLocation().x));
				properties.setProperty(SHELL_LOCATION_Y, Integer.toString(shell.getLocation().y));
			}

			// Save sash weights
			final SashForm sashForm = gui.getSashForm();
			if (sashForm != null && !sashForm.isDisposed()) {
				for (int i = 0; i < sashForm.getWeights().length; i++) {
					properties.setProperty(SHELL_SASH_WEIGHT + '.' + i, Integer.toString(sashForm.getWeights()[i]));
				}
			}

			try (final OutputStream os = new FileOutputStream(configuration.getFileName())) {
				properties.store(os, null); // save configuration
			}
			catch (final IOException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {/* Ignore */}

}
