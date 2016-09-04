package it.albertus.geofon.client.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

public class MapCache {

	private final Map<String, Image> cache = new LinkedHashMap<String, Image>();

	public void put(final String guid, final Image map) {
		cache.put(guid, map);
	}

	public Image get(final String guid) {
		return cache.get(guid);
	}

	public boolean contains(final String guid) {
		return cache.containsKey(guid);
	}

}
