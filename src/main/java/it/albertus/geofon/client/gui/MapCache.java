package it.albertus.geofon.client.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

public class MapCache {

	private final Map<String, Image> cache = new LinkedHashMap<String, Image>(10);

	public void put(final String guid, final Image map) {
		if (cache.size() >= 10) { // TODO configure 
			final String eldestGuid = cache.keySet().iterator().next();
			final Image eldestImage = cache.get(eldestGuid);
			cache.remove(eldestGuid);
			eldestImage.dispose();
		}
		cache.put(guid, map);
	}

	public Image get(final String guid) {
		return cache.get(guid);
	}

	public boolean contains(final String guid) {
		return cache.containsKey(guid);
	}

	@Override
	public String toString() {
		return "MapCache [cache=" + cache + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cache == null) ? 0 : cache.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MapCache)) {
			return false;
		}
		MapCache other = (MapCache) obj;
		if (cache == null) {
			if (other.cache != null) {
				return false;
			}
		}
		else if (!cache.equals(other.cache)) {
			return false;
		}
		return true;
	}

}
