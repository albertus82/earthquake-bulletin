package it.albertus.earthquake.gui.listener;

import it.albertus.earthquake.gui.SearchForm;
import it.albertus.earthquake.map.MapDialog;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class MapButtonSelectionListener extends SelectionAdapter {

	private final SearchForm form;

	public MapButtonSelectionListener(final SearchForm form) {
		this.form = form;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final MapDialog mapDialog = new MapDialog(form);
		mapDialog.open();
	}

}
