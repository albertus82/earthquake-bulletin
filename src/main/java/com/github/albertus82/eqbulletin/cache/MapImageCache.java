package com.github.albertus82.eqbulletin.cache;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import com.github.albertus82.eqbulletin.gui.preference.Preference;
import com.github.albertus82.eqbulletin.model.MapImage;

import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MapImageCache implements Cache<String, MapImage> {

	private static final long serialVersionUID = 14746911870762927L;

	private static final String CACHE_FILE = CacheManager.CACHE_DIRECTORY + File.separator + "mapcache.ser";

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final byte CACHE_SIZE = 20;
		public static final boolean CACHE_SAVE = true;
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private static final CacheManager<MapImageCache> manager = new CacheManager<>();

	private static MapImageCache instance;

	public static synchronized MapImageCache getInstance() {
		if (instance == null) {
			if (configuration.getBoolean(Preference.MAP_CACHE_SAVE, Defaults.CACHE_SAVE)) {
				instance = manager.deserialize(CACHE_FILE, MapImageCache.class);
			}
			else {
				manager.delete(CACHE_FILE);
			}
			if (instance == null) {
				instance = new MapImageCache();
			}
		}
		return instance;
	}

	private final Map<String, MapImage> cache = new LinkedHashMap<>(16, 0.75f, true);

	@Override
	public synchronized void put(final String guid, final MapImage map) {
		cache.put(guid, map);
		while (cache.size() > 0 && cache.size() > configuration.getByte(Preference.MAP_CACHE_SIZE, Defaults.CACHE_SIZE)) {
			final String firstKey = cache.keySet().iterator().next();
			cache.remove(firstKey);
		}
		if (configuration.getBoolean(Preference.MAP_CACHE_SAVE, Defaults.CACHE_SAVE)) {
			manager.serialize(instance, CACHE_FILE);
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
