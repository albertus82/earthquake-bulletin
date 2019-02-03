package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.TypedEvent;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MenuBar;
import it.albertus.eqbulletin.model.Earthquake;

public class EventMenuListener implements ArmMenuListener {

	private final EarthquakeBulletinGui gui;

	public EventMenuListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void menuArmed(final TypedEvent e) {
		final Earthquake selection = (Earthquake) gui.getResultsTable().getTableViewer().getStructuredSelection().getFirstElement();
		final MenuBar menuBar = gui.getMenuBar();
		menuBar.getShowMapMenuItem().setEnabled(selection != null && selection.getEnclosureUri() != null);
		menuBar.getShowMomentTensorMenuItem().setEnabled(selection != null && selection.getMomentTensorUri() != null);
		menuBar.getCopyLinkMenuItem().setEnabled(selection != null && selection.getLink() != null);
		menuBar.getOpenBrowserMenuItem().setEnabled(selection != null && selection.getLink() != null);
		menuBar.getGoogleMapsBrowserMenuItem().setEnabled(selection != null);
		menuBar.getEpicenterMapPopupMenuItem().setEnabled(selection != null);
	}

}
