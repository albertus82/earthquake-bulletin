package it.albertus.earthquake.gui;

import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.util.Configuration;

public class MapCache {

	public static class Defaults {
		public static final byte CACHE_SIZE = 20;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final String CFG_KEY_MAP_CACHE_SIZE = "map.cache.size";

	private static final Configuration configuration = EarthquakeBulletin.getConfiguration();

	private final Map<String, byte[]> cache = new LinkedHashMap<>(configuration.getByte(CFG_KEY_MAP_CACHE_SIZE, Defaults.CACHE_SIZE));

	public void put(final String guid, final byte[] map) {
		if (!cache.containsKey(guid)) {
			cache.put(guid, map);
		}
		while (cache.size() > 0 && cache.size() > configuration.getByte(CFG_KEY_MAP_CACHE_SIZE, Defaults.CACHE_SIZE)) {
			final String eldestGuid = cache.keySet().iterator().next();
			cache.remove(eldestGuid);
		}
	}

	public byte[] get(final String guid) {
		return cache.get(guid);
	}

	public boolean contains(final String guid) {
		return cache.containsKey(guid);
	}

	public int size() {
		return cache.size();
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
