package it.albertus.geofon.client.gui.job;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.SearchForm;
import it.albertus.geofon.client.model.Earthquake;
import it.albertus.geofon.client.rss.transformer.ItemTransformer;
import it.albertus.geofon.client.rss.xml.Item;
import it.albertus.geofon.client.rss.xml.Rss;
import it.albertus.jface.SwtThreadExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

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

		final Map<String, String> params = new LinkedHashMap<>();

		new SwtThreadExecutor(gui.getShell()) {
			@Override
			protected void run() {
				gui.getSearchForm().getSearchButton().setEnabled(false);
				gui.getShell().setCursor(gui.getShell().getDisplay().getSystemCursor(SWT.CURSOR_WAIT));

				// Parametri di ricerca
				final SearchForm form = gui.getSearchForm();
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

				if (gui.getSearchForm().getAutoRefreshButton().getSelection()) {
					try {
						int waitTimeInSeconds = Integer.parseInt(gui.getSearchForm().getAutoRefreshText().getText());
						if (waitTimeInSeconds > 0) {
							gui.getSearchForm().getStopButton().setEnabled(true);
						}
					}
					catch (final RuntimeException re) {
						re.printStackTrace();
					}
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
			HttpURLConnection urlConnection = openConnection(url, 5000, 5000);
			is = urlConnection.getInputStream();
			final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			rss = (Rss) jaxbUnmarshaller.unmarshal(is);
			urlConnection.disconnect();
		}
		catch (final IOException | JAXBException e) {
			e.printStackTrace(); // TODO error message
		}
		finally {
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
		}

		if (rss != null && rss.getChannel() != null) {
			final List<Earthquake> earthquakes = new ArrayList<>();
			if (rss.getChannel().getItem() != null) {
				for (final Item item : rss.getChannel().getItem()) {
					earthquakes.add(ItemTransformer.fromRss(item));
				}
			}
			new SwtThreadExecutor(gui.getShell()) {
				@Override
				protected void run() {
					gui.getResultTable().getTableViewer().setInput(earthquakes.toArray(new Earthquake[0]));
				}
			}.start();

		}

		new SwtThreadExecutor(gui.getShell()) {
			@Override
			protected void run() {
				if (gui.getSearchForm().getAutoRefreshButton().getSelection()) {
					try {
						int waitTimeInSeconds = Integer.parseInt(gui.getSearchForm().getAutoRefreshText().getText());
						if (waitTimeInSeconds > 0) {
							schedule(waitTimeInSeconds * 1000);
						}
					}
					catch (final RuntimeException re) {
						re.printStackTrace();
						gui.getSearchForm().getSearchButton().setEnabled(true);
						gui.getSearchForm().getStopButton().setEnabled(false);
					}
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

	private HttpURLConnection openConnection(final URL url, final int connectionTimeout, final int readTimeout) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(connectionTimeout);
		urlConnection.setReadTimeout(readTimeout);
		urlConnection.addRequestProperty("Accept", "text/xml");
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

	public void setShouldRun(boolean shouldRun) {
		this.shouldRun = shouldRun;
	}

	public void setShouldSchedule(boolean shouldSchedule) {
		this.shouldSchedule = shouldSchedule;
	}

}
