package it.albertus.earthquake.gui.listener;

import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;
import it.albertus.earthquake.model.Earthquake;

public class FileMenuArmListener implements ArmListener {

	private final EarthquakeBulletinGui gui;

	public FileMenuArmListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetArmed(final ArmEvent e) {
		if (gui.getResultsTable() != null && gui.getResultsTable().getTableViewer() != null) {
			final Object input = gui.getResultsTable().getTableViewer().getInput();
			final boolean enabled = input instanceof Earthquake[] && ((Earthquake[]) input).length > 0;
			gui.getMenuBar().getFileExportCsvItem().setEnabled(enabled);
		}
	}

}
