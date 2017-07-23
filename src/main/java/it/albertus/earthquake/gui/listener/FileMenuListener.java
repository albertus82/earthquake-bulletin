package it.albertus.earthquake.gui.listener;

import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;

import it.albertus.earthquake.gui.EarthquakeBulletinGui;

public class FileMenuListener implements ArmListener, MenuListener {

	private final EarthquakeBulletinGui gui;

	public FileMenuListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
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
		gui.getMenuBar().getFileExportCsvItem().setEnabled(gui.getResultsTable() != null && gui.getResultsTable().getTableViewer() != null && gui.getResultsTable().getTableViewer().getTable() != null && gui.getResultsTable().getTableViewer().getTable().getItemCount() > 0);
	}

}
