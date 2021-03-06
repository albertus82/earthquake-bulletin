package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.TypedEvent;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.gui.MenuBar;
import it.albertus.eqbulletin.model.Earthquake;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EventMenuListener implements ArmMenuListener {

	private final @NonNull EarthquakeBulletinGui gui;

	@Override
	public void menuArmed(final TypedEvent e) {
		final Earthquake selection = (Earthquake) gui.getResultsTable().getTableViewer().getStructuredSelection().getFirstElement();
		final MenuBar menuBar = gui.getMenuBar();
		menuBar.getShowMapMenuItem().setEnabled(selection != null && selection.getEnclosureUri().isPresent());
		menuBar.getShowMomentTensorMenuItem().setEnabled(selection != null && selection.getMomentTensorUri().isPresent());
		menuBar.getCopyLinkMenuItem().setEnabled(selection != null && selection.getLink().isPresent());
		menuBar.getOpenBrowserMenuItem().setEnabled(selection != null && selection.getLink().isPresent());
		menuBar.getGoogleMapsBrowserMenuItem().setEnabled(selection != null);
		menuBar.getEpicenterMapPopupMenuItem().setEnabled(selection != null);
	}

}
