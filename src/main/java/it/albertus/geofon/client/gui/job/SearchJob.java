package it.albertus.geofon.client.gui.job;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.SearchForm;
import it.albertus.geofon.client.model.Earthquake;
import it.albertus.geofon.client.model.Format;
import it.albertus.geofon.client.net.HttpConnector;
import it.albertus.geofon.client.rss.transformer.ItemTransformer;
import it.albertus.geofon.client.rss.xml.Item;
import it.albertus.geofon.client.rss.xml.Rss;
import it.albertus.jface.SwtThreadExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import com.dmurph.URIEncoder;

public class SearchJob extends Job {

	private final GeofonClientGui gui;
	private boolean shouldRun = true;
	private boolean shouldSchedule = true;

	public SearchJob(final GeofonClientGui gui) {
		super("Search");
		this.gui = gui;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Search", 1);

		final long[] waitTimeInMillis = new long[1];

		final Map<String, String> params = new LinkedHashMap<>();

		new SwtThreadExecutor(gui.getShell()) {
			@Override
			protected void run() {
				gui.getSearchForm().getSearchButton().setEnabled(false);
				gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));

				// Parametri di ricerca
				final SearchForm form = gui.getSearchForm();
				for (final Entry<Format, Button> entry : form.getFormatRadios().entrySet()) {
					if (entry.getValue().getSelection()) {
						params.put("fmt", entry.getKey().toString().toLowerCase());
						break;
					}
				}
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

				if (gui.getSearchForm().getAutoRefreshButton().getSelection()) {
					try {
						short waitTimeInMinutes = Short.parseShort(gui.getSearchForm().getAutoRefreshText().getText());
						if (waitTimeInMinutes > 0) {
							waitTimeInMillis[0] = waitTimeInMinutes * 1000 * 60;
							gui.getSearchForm().getStopButton().setEnabled(true);
						}
					}
					catch (final RuntimeException re) {/* Ignore */}
				}
			}
		}.start();

		Rss rss = null;
		InputStream is = null;
		try {
			final StringBuilder urlSb = new StringBuilder("http://geofon.gfz-potsdam.de/eqinfo/list.php?fmt=").append(params.get("fmt"));
			for (final Entry<String, String> param : params.entrySet()) {
				if (param.getValue() != null && !param.getValue().isEmpty() && !param.getKey().equals("fmt")) {
					urlSb.append("&").append(param.getKey()).append("=").append(param.getValue());
				}
			}

			final URL url = new URL(urlSb.toString());
			HttpURLConnection urlConnection = openConnection(url);
			is = urlConnection.getInputStream();
			final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			rss = (Rss) jaxbUnmarshaller.unmarshal(is);
			urlConnection.disconnect();
		}
		catch (final Exception e) {
			e.printStackTrace(); // TODO error message
		}
		finally {
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
		}

		if (rss != null && rss.getChannel() != null) {
			final Set<Earthquake> earthquakes = new TreeSet<>();
			if (rss.getChannel().getItem() != null) {
				for (final Item item : rss.getChannel().getItem()) {
					earthquakes.add(ItemTransformer.fromRss(item));
				}
			}
			new SwtThreadExecutor(gui.getShell()) {
				@Override
				protected void run() {
					final Earthquake[] newData = earthquakes.toArray(new Earthquake[0]);
					final Earthquake[] oldData = (Earthquake[]) gui.getResultsTable().getTableViewer().getInput();
					gui.getResultsTable().getTableViewer().setInput(newData);
					if (oldData != null && !Arrays.equals(newData, oldData) && newData[0] != null) {
						gui.getMapCanvas().clear();
						gui.getTrayIcon().showBalloonToolTip(newData[0]);
					}
				}
			}.start();
		}

		new SwtThreadExecutor(gui.getShell()) {
			@Override
			protected void run() {
				if (waitTimeInMillis[0] > 0) {
					schedule(waitTimeInMillis[0]);
				}
				else {
					gui.getSearchForm().getSearchButton().setEnabled(true);
					gui.getSearchForm().getStopButton().setEnabled(false);
				}
				gui.getShell().setCursor(null);
			}
		}.start();

		monitor.done();
		return Status.OK_STATUS;
	}

	private HttpURLConnection openConnection(final URL url) throws IOException {
		final HttpURLConnection urlConnection = HttpConnector.openConnection(url);
		urlConnection.addRequestProperty("Accept", "*/xml");
		return urlConnection;
	}

	@Override
	public boolean shouldSchedule() {
		return shouldSchedule;
	}

	@Override
	public boolean shouldRun() {
		return shouldRun;
	}

	public void setShouldRun(final boolean shouldRun) {
		this.shouldRun = shouldRun;
	}

	public void setShouldSchedule(final boolean shouldSchedule) {
		this.shouldSchedule = shouldSchedule;
	}

}
