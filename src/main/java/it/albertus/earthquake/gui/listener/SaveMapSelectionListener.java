package it.albertus.earthquake.gui.listener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;

import it.albertus.earthquake.gui.MapCanvas;
import it.albertus.earthquake.resources.Messages;
import it.albertus.util.logging.LoggerFactory;

public class SaveMapSelectionListener extends SelectionAdapter {

	private static final Logger logger = LoggerFactory.getLogger(SaveMapSelectionListener.class);

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
					Files.write(Paths.get(fileName), mapCanvas.getCache().get(guid).getBytes());
				}
				catch (final Exception e) {
					final String message = Messages.get("err.image.save", fileName);
					logger.log(Level.WARNING, message, e);
					final MessageBox dialog = new MessageBox(mapCanvas.getCanvas().getShell(), SWT.ICON_WARNING);
					dialog.setText(Messages.get("lbl.window.title"));
					dialog.setMessage(message);
					dialog.open();
				}
			}
		}
	}

}
