package it.albertus.eqbulletin.cache;

import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.jface.preference.IPreferencesConfiguration;

public class MapImageCache implements Cache<String, MapImage> {

	public static class Defaults {
		public static final byte CACHE_SIZE = 20;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private static MapImageCache instance;

	public static synchronized MapImageCache getInstance() {
		if (instance == null) {
			instance = new MapImageCache();
		}
		return instance;
	}

	private MapImageCache() {}

	private final Map<String, MapImage> cache = new LinkedHashMap<>(16, 0.75f, true);

	@Override
	public synchronized void put(final String guid, final MapImage map) {
		cache.put(guid, map);
		while (cache.size() > 0 && cache.size() > configuration.getByte(Preference.MAP_CACHE_SIZE, Defaults.CACHE_SIZE)) {
			final String firstKey = cache.keySet().iterator().next();
			cache.remove(firstKey);
		}
	}

	@Override
	public synchronized MapImage get(final String guid) {
		return cache.get(guid);
	}

	@Override
	public synchronized boolean contains(final String guid) {
		return cache.containsKey(guid);
	}

	@Override
	public int getSize() {
		return cache.size();
	}

	@Override
	public String toString() {
		return "MapImageCache [size=" + getSize() + "]";
	}

}
