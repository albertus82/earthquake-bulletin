package it.albertus.eqbulletin.gui.listener;

import java.net.URI;
import java.util.Optional;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

import it.albertus.eqbulletin.gui.EarthquakeBulletinGui;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.jface.closeable.CloseableClipboard;

public class CopyLinkSelectionListener extends SelectionAdapter {

	private final EarthquakeBulletinGui gui;

	public CopyLinkSelectionListener(final EarthquakeBulletinGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final TableViewer tableViewer = gui.getResultsTable().getTableViewer();
		final Earthquake selection = (Earthquake) tableViewer.getStructuredSelection().getFirstElement();
		final Table table = tableViewer.getTable();
		if (selection != null && table != null && !table.isDisposed()) {
			final Optional<URI> link = selection.getLink();
			if (link.isPresent()) {
				try (final CloseableClipboard cc = new CloseableClipboard(new Clipboard(table.getDisplay()))) {
					cc.getClipboard().setContents(new String[] { link.get().toString() }, new Transfer[] { TextTransfer.getInstance() });
				}
			}
		}
	}

}
