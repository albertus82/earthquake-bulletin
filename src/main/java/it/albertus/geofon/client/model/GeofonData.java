package it.albertus.geofon.client.model;

import it.albertus.geofon.client.xml.Rss;

public class GeofonData {

	private Rss rss;

	public Rss getRss() {
		return rss;
	}

	public void setRss(Rss rss) {
		this.rss = rss;
	}

	@Override
	public String toString() {
		return "GeofonData [" + (rss != null ? "rss=" + rss : "") + "]";
	}

}
