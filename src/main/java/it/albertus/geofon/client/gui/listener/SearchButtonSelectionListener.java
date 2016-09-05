package it.albertus.geofon.client.gui.listener;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.job.SearchJob;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class SearchButtonSelectionListener extends SelectionAdapter {

	private static final String CHARSET = "UTF-8";

	private final GeofonClientGui gui;

	public SearchButtonSelectionListener(final GeofonClientGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if ((gui.getJob() == null || gui.getJob().getState() == Job.NONE)) {
			// Disabilitazione controlli durante la ricerca
			// gui.disableControls();

			// Impostazione puntatore del mouse "Occupato"
			gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));

			// Parametri di ricerca
			final Map<String, String> params = new LinkedHashMap<String, String>();
			params.put("fmt", "rss"); // TODO
			params.put("mode", gui.getSearchForm().getRestrictButton().getSelection() ? "mt" : "");
			try {
				if (gui.getSearchForm().getPeriodFromText().isEnabled()) {
					params.put("datemin", URLEncoder.encode(gui.getSearchForm().getPeriodFromText().getText(), CHARSET));
				}
				if (gui.getSearchForm().getPeriodFromText().isEnabled()) {
					params.put("datemax", URLEncoder.encode(gui.getSearchForm().getPeriodFromText().getText(), CHARSET));
				}
				params.put("latmin", URLEncoder.encode(gui.getSearchForm().getLatitudeFromText().getText(), CHARSET));
				params.put("latmax", URLEncoder.encode(gui.getSearchForm().getLatitudeToText().getText(), CHARSET));
				params.put("lonmin", URLEncoder.encode(gui.getSearchForm().getLongitudeFromText().getText(), CHARSET));
				params.put("lonmax", URLEncoder.encode(gui.getSearchForm().getLongitudeToText().getText(), CHARSET));
				params.put("magmin", URLEncoder.encode(gui.getSearchForm().getMinimumMagnitudeText().getText(), CHARSET));
				params.put("nmax", URLEncoder.encode(gui.getSearchForm().getResultsText().getText(), CHARSET));
			}
			catch (final UnsupportedEncodingException use) {
				use.printStackTrace();
			}
			// Avvio della ricerca
			gui.setJob(new SearchJob(gui, params));
			gui.getJob().schedule();
		}
	}

}
