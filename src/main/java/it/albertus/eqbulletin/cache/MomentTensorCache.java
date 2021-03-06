package it.albertus.eqbulletin.cache;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MomentTensorCache implements Cache<String, MomentTensor> {

	private static final long serialVersionUID = -3278285082043132654L;

	private static final String CACHE_FILE = CacheManager.CACHE_DIRECTORY + File.separator + "mtcache.ser";

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Defaults {
		public static final byte CACHE_SIZE = 20;
		public static final boolean CACHE_SAVE = true;
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	private static final CacheManager<MomentTensorCache> manager = new CacheManager<>();

	private static MomentTensorCache instance;

	public static synchronized MomentTensorCache getInstance() {
		if (instance == null) {
			if (configuration.getBoolean(Preference.MT_CACHE_SAVE, Defaults.CACHE_SAVE)) {
				instance = manager.deserialize(CACHE_FILE, MomentTensorCache.class);
			}
			else {
				manager.delete(CACHE_FILE);
			}
			if (instance == null) {
				instance = new MomentTensorCache();
			}
		}
		return instance;
	}

	private final Map<String, PackedMomentTensor> cache = new LinkedHashMap<>(16, 0.75f, true);

	@Override
	public synchronized void put(final String guid, final MomentTensor momentTensor) {
		cache.put(guid, PackedMomentTensor.pack(momentTensor));
		while (cache.size() > 0 && cache.size() > configuration.getByte(Preference.MT_CACHE_SIZE, Defaults.CACHE_SIZE)) {
			final String firstKey = cache.keySet().iterator().next();
			cache.remove(firstKey);
		}
		if (configuration.getBoolean(Preference.MT_CACHE_SAVE, Defaults.CACHE_SAVE)) {
			manager.serialize(instance, CACHE_FILE);
		}
	}

	@Override
	public synchronized MomentTensor get(final String guid) {
		final PackedMomentTensor element = cache.get(guid);
		return element != null ? element.unpack() : null;
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
		return "MomentTensorCache [size=" + getSize() + "]";
	}

}
