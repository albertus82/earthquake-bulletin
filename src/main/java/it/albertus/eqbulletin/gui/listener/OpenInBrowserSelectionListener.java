package it.albertus.eqbulletin.gui.listener;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.program.Program;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.model.Earthquake;

public class OpenInBrowserSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public OpenInBrowserSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final Earthquake selection = (Earthquake) gui.getResultsTable().getTableViewer().getStructuredSelection().getFirstElement();
		if (selection != null && selection.getLink() != null) {
			Program.launch(selection.getLink().toString());
		}
	}

}
