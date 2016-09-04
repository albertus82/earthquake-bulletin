package it.albertus.geofon.client;

import it.albertus.geofon.client.gui.GeofonClientGui;
import it.albertus.geofon.client.model.Earthquake;
import it.albertus.geofon.client.rss.transformer.ItemTransformer;
import it.albertus.geofon.client.rss.xml.Item;
import it.albertus.geofon.client.rss.xml.Rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.swt.widgets.Display;

public class GeofonClient {

	public static void main(String[] args) {
		final Display display = Display.getDefault();
		new GeofonClientGui(display);
		display.dispose();
	}

	private void run() {
		Rss rss = null;
		InputStream is = null;
		try {
			URL url = new URL("http://geofon.gfz-potsdam.de/eqinfo/list.php?fmt=rss");//&latmin=40&latmax=44&lonmin=10&lonmax=14&magmin=");
			HttpURLConnection urlConnection = openConnection(url, 5000, 5000);
			is = urlConnection.getInputStream();
			final JAXBContext jaxbContext = JAXBContext.newInstance(Rss.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			rss = (Rss) jaxbUnmarshaller.unmarshal(is);
			urlConnection.disconnect();
		}
		catch (final IOException | JAXBException e) {
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			}
			catch (final Exception e) {/* Ignore */}
		}
		System.out.println(rss);

		List<Earthquake> earthquakes = new ArrayList<>();
		for (final Item item : rss.getChannel().getItem()) {
			earthquakes.add(ItemTransformer.fromRss(item));
		}

		Collections.sort(earthquakes);
		for (final Earthquake earthquake : earthquakes) {
			System.out.println(earthquake);
		}
	}

	private HttpURLConnection openConnection(final URL url, final int connectionTimeout, final int readTimeout) throws IOException {
		final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setConnectTimeout(connectionTimeout);
		urlConnection.setReadTimeout(readTimeout);
		urlConnection.addRequestProperty("Accept", "text/xml");
		return urlConnection;
	}

}
