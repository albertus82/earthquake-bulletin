package it.albertus.earthquake.gui.listener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import it.albertus.earthquake.config.EarthquakeBulletinConfiguration;
import it.albertus.earthquake.gui.CloseDialog;
import it.albertus.util.Configuration;
import it.albertus.util.logging.LoggerFactory;

public class CloseListener implements Listener, SelectionListener {

	private static final Logger logger = LoggerFactory.getLogger(CloseListener.class);

	private static final Configuration configuration = EarthquakeBulletinConfiguration.getInstance();

	private final IShellProvider provider;

	public CloseListener(final IShellProvider provider) {
		this.provider = provider;
	}

	private boolean canClose() {
		return !CloseDialog.mustShow() || CloseDialog.open(provider.getShell()) == SWT.YES;
	}

	private void disposeShellAndDisplay() {
		final Shell shell = provider.getShell();
		if (shell != null && !shell.isDisposed()) {
			saveShellStatus(shell);
			shell.dispose();
		}
		final Display display = Display.getCurrent();
		if (display != null) {
			display.dispose(); // Fix close not working on Windows 10 when iconified
		}
	}

	/* Shell close command & OS X Menu */
	@Override
	public void handleEvent(final Event event) {
		if (canClose()) {
			disposeShellAndDisplay();
		}
		else if (event != null) {
			event.doit = false;
		}
	}

	/* Menu */
	@Override
	public void widgetSelected(final SelectionEvent event) {
		if (canClose()) {
			disposeShellAndDisplay();
		}
		else if (event != null) {
			event.doit = false;
		}
	}

	private static void saveShellStatus(final Shell shell) {
		if (shell.getSize() != null && shell.getLocation() != null && configuration != null && configuration.getFileName() != null) {
			final Properties properties = new Properties();
			try (final FileInputStream fis = new FileInputStream(configuration.getFileName())) {
				properties.load(fis);
			}
			catch (final IOException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}

			properties.setProperty("shell.size.x", Integer.toString(shell.getSize().x));
			properties.setProperty("shell.size.y", Integer.toString(shell.getSize().y));
			properties.setProperty("shell.location.x", Integer.toString(shell.getLocation().x));
			properties.setProperty("shell.location.y", Integer.toString(shell.getLocation().y));
			properties.setProperty("shell.maximized", Boolean.toString(shell.getMaximized()));

			try (final FileOutputStream fos = new FileOutputStream(configuration.getFileName())) {
				properties.store(fos, null);
				logger.log(Level.FINE, "Shell size [{0}], location [{1}] & maximized [{2}] saved into {3}", new Object[] { shell.getSize(), shell.getLocation(), shell.getMaximized(), configuration.getFileName() });
			}
			catch (final IOException e) {
				logger.log(Level.WARNING, e.toString(), e);
			}
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {/* Ignore */}

}
