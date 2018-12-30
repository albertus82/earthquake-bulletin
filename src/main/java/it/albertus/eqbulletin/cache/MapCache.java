package it.albertus.eqbulletin.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.jface.preference.IPreferencesConfiguration;

public class MapCache {

	public static class Defaults {
		public static final byte CACHE_SIZE = 20;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private static MapCache instance;

	public static synchronized MapCache getInstance() {
		if (instance == null) {
			instance = new MapCache();
		}
		return instance;
	}

	private MapCache() {}

	private final Map<String, MapImage> cache = new LinkedHashMap<>();

	public void put(final String guid, final MapImage map) {
		cache.put(guid, map);
		while (cache.size() > 0 && cache.size() > configuration.getByte(Preference.MAP_CACHE_SIZE, Defaults.CACHE_SIZE)) {
			final String eldestGuid = cache.keySet().iterator().next();
			cache.remove(eldestGuid);
		}
	}

	public MapImage get(final String guid) {
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
		return "MapCache [size=" + size() + "]";
	}

}
