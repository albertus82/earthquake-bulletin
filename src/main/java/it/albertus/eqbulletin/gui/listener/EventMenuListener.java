package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MenuBar;
import it.albertus.eqbulletin.model.Earthquake;

public class EventMenuListener implements ArmListener, MenuListener {

	private final EarthquakeBulletinGui gui;

	public EventMenuListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetArmed(final ArmEvent e) {
		manageItems();
	}

	@Override
	public void menuShown(final MenuEvent e) {
		manageItems();
	}

	@Override
	public void menuHidden(final MenuEvent e) {/* Ignore */}

	private void manageItems() {
		final Earthquake selection = (Earthquake) gui.getResultsTable().getTableViewer().getStructuredSelection().getFirstElement();
		final MenuBar menuBar = gui.getMenuBar();
		menuBar.getShowMapMenuItem().setEnabled(selection != null && selection.getEnclosure() != null);
		menuBar.getCopyLinkMenuItem().setEnabled(selection != null && selection.getLink() != null);
		menuBar.getOpenBrowserMenuItem().setEnabled(selection != null && selection.getLink() != null);
		menuBar.getGoogleMapsBrowserMenuItem().setEnabled(selection != null);
		menuBar.getGoogleMapsPopupMenuItem().setEnabled(selection != null);
	}

}
