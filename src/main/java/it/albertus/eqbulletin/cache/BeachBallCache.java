package it.albertus.eqbulletin.cache;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.BeachBall;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BeachBallCache implements Cache<String, BeachBall> {

	private static final long serialVersionUID = 6219702620502135946L;

	private static final String CACHE_FILE = CacheManager.CACHE_DIRECTORY + File.separator + "mticache.ser";

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final byte CACHE_SIZE = 20;
		public static final boolean CACHE_SAVE = true;
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private static final CacheManager<BeachBallCache> manager = new CacheManager<>();

	private static BeachBallCache instance;

	public static synchronized BeachBallCache getInstance() {
		if (instance == null) {
			if (configuration.getBoolean(Preference.MTI_CACHE_SAVE, Defaults.CACHE_SAVE)) {
				instance = manager.deserialize(CACHE_FILE, BeachBallCache.class);
			}
			else {
				manager.delete(CACHE_FILE);
			}
			if (instance == null) {
				instance = new BeachBallCache();
			}
		}
		return instance;
	}

	private final Map<String, BeachBall> cache = new LinkedHashMap<>(16, 0.75f, true);

	@Override
	public synchronized void put(final String guid, final BeachBall image) {
		cache.put(guid, image);
		while (cache.size() > 0 && cache.size() > configuration.getByte(Preference.MTI_CACHE_SIZE, Defaults.CACHE_SIZE)) {
			final String firstKey = cache.keySet().iterator().next();
			cache.remove(firstKey);
		}
		if (configuration.getBoolean(Preference.MTI_CACHE_SAVE, Defaults.CACHE_SAVE)) {
			manager.serialize(instance, CACHE_FILE);
		}
	}

	@Override
	public synchronized BeachBall get(final String guid) {
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
		return "BeachBallCache [size=" + getSize() + "]";
	}

}
