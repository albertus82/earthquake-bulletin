package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.MapCanvas;

import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;

public class SaveMapSelectionListener extends SelectionAdapter {

	private final MapCanvas mapCanvas;

	public SaveMapSelectionListener(final MapCanvas mapCanvas) {
		this.mapCanvas = mapCanvas;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final String guid = mapCanvas.getGuid();
		if (guid != null && mapCanvas.getImage() != null) {
			final FileDialog saveDialog = new FileDialog(mapCanvas.getCanvas().getShell(), SWT.SAVE);
			saveDialog.setFilterExtensions(new String[] { "*.JPG;*.jpg" });
			saveDialog.setFileName(guid.toLowerCase() + ".jpg");
			saveDialog.setOverwrite(true);
			final String fileName = saveDialog.open();
			if (fileName != null) {
				try {
					Files.write(Paths.get(fileName), mapCanvas.getCache().get(guid));
				}
				catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
