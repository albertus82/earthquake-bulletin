package it.albertus.geofon.client.gui.job;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.gui.SearchResultPrinter;
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

public class SearchJob extends Job {

	private final GeofonClientGui gui;
	private final Map<String, String> params;

	public SearchJob(final GeofonClientGui gui, final Map<String, String> params) {
		super("Search");
		this.gui = gui;
		this.params = params;
		this.setUser(true);
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		monitor.beginTask("Search", 1);

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
			new SwtThreadExecutor(gui.getShell()) {
				@Override
				protected void run() {
					gui.getShell().setCursor(null);
				}
			}.start();
		}
		finally {
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
		}

		final List<Earthquake> earthquakes = new ArrayList<>();
		for (final Item item : rss.getChannel().getItem()) {
			earthquakes.add(ItemTransformer.fromRss(item));
		}

		gui.getShell().getDisplay().syncExec(new SearchResultPrinter(gui, earthquakes));

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

}
