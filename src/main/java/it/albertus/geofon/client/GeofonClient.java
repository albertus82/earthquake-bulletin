package it.albertus.geofon.client;

import it.albertus.geofon.client.model.GeofonData;
import it.albertus.geofon.client.model.ItemDescription;
import it.albertus.geofon.client.xml.Item;
import it.albertus.geofon.client.xml.Rss;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class GeofonClient {

	public static void main(String[] args) {
		new GeofonClient().run();
	}

	private void run() {
		GeofonData geofonData = null;
		InputStream is = null;
		try {
			URL url = new URL("http://geofon.gfz-potsdam.de/eqinfo/list.php?latmin=40&latmax=44&lonmin=10&lonmax=14&magmin=&fmt=rss");
			HttpURLConnection urlConnection = openConnection(url, 5000, 5000);
			is = urlConnection.getInputStream();
			final JAXBContext jaxbContext = JAXBContext.newInstance(GeofonData.class);
			final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			final Rss rss = (Rss) jaxbUnmarshaller.unmarshal(is);
			geofonData = new GeofonData();
			geofonData.setRss(rss);
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
		System.out.println(geofonData);
		for (Item item : geofonData.getRss().getChannel().getItem()) {
			System.out.println(new ItemDescription(item.getDescription()));
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
