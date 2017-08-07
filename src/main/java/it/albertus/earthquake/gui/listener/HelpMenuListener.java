package it.albertus.earthquake.gui.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.MenuItem;

import it.albertus.util.logging.LoggerFactory;

public class HelpMenuListener implements ArmListener, MenuListener {

	private static final Logger logger = LoggerFactory.getLogger(HelpMenuListener.class);

	private final MenuItem item;

	public HelpMenuListener(final MenuItem item) {
		this.item = item;
	}

	@Override
	public void widgetArmed(final ArmEvent e) {
		execute();
	}

	@Override
	public void menuShown(final MenuEvent e) {
		execute();
	}

	@Override
	public void menuHidden(final MenuEvent e) {/* Ignore */}

	private void execute() {
		final SecurityManager securityManager = System.getSecurityManager();
		if (securityManager != null) {
			try {
				securityManager.checkPropertiesAccess();
			}
			catch (final SecurityException e) {
				logger.log(Level.FINE, e.toString(), e);
				item.setEnabled(false);
			}
		}
	}

}
