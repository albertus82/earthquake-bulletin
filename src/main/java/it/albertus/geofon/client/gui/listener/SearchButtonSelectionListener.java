package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.SearchForm;
import it.albertus.geofon.client.gui.job.SearchJob;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import com.dmurph.URIEncoder;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private final GeofonClientGui gui;

	public SearchButtonSelectionListener(final GeofonClientGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		if ((gui.getJob() == null || gui.getJob().getState() == Job.NONE)) {
			// Disabilitazione controlli durante la ricerca
			// gui.disableControls();

			// Impostazione puntatore del mouse "Occupato"
			gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));

			// Parametri di ricerca
			final SearchForm form = gui.getSearchForm();
			final Map<String, String> params = new LinkedHashMap<String, String>();
			params.put("fmt", "rss"); // TODO
			params.put("mode", form.getRestrictButton().getSelection() ? "mt" : "");
			if (form.getPeriodFromText().isEnabled()) {
				params.put("datemin", URIEncoder.encodeURI(form.getPeriodFromText().getText()));
			}
			if (form.getPeriodFromText().isEnabled()) {
				params.put("datemax", URIEncoder.encodeURI(form.getPeriodFromText().getText()));
			}
			params.put("latmin", URIEncoder.encodeURI(form.getLatitudeFromText().getText()));
			params.put("latmax", URIEncoder.encodeURI(form.getLatitudeToText().getText()));
			params.put("lonmin", URIEncoder.encodeURI(form.getLongitudeFromText().getText()));
			params.put("lonmax", URIEncoder.encodeURI(form.getLongitudeToText().getText()));
			params.put("magmin", URIEncoder.encodeURI(form.getMinimumMagnitudeText().getText()));
			params.put("nmax", URIEncoder.encodeURI(form.getResultsText().getText()));

			// Avvio della ricerca
			gui.setJob(new SearchJob(gui, params));
			gui.getJob().schedule();
		}
	}

}
