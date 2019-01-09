package it.albertus.eqbulletin.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorCache implements Cache<String, MomentTensor> {

	private static final long serialVersionUID = -6126159757256952811L;

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorCache.class);

	public static class Defaults {
		public static final byte CACHE_SIZE = 20;
		public static final boolean CACHE_SAVE = true;

		private Defaults() {
			throw new IllegalAccessError("Constants class");
		}
	}

	private static final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private static MomentTensorCache instance;

	public static synchronized MomentTensorCache getInstance() {
		if (instance == null) {
			if (configuration.getBoolean(Preference.MT_CACHE_SAVE, Defaults.CACHE_SAVE)) {
				instance = deserialize();
			}
			if (instance == null) {
				instance = new MomentTensorCache();
			}
		}
		return instance;
	}

	private MomentTensorCache() {}

	private final Map<String, PackedMomentTensor> cache = new LinkedHashMap<>(16, 0.75f, true);

	@Override
	public synchronized void put(final String guid, final MomentTensor momentTensor) {
		cache.put(guid, PackedMomentTensor.pack(momentTensor));
		while (cache.size() > 0 && cache.size() > configuration.getByte(Preference.MT_CACHE_SIZE, Defaults.CACHE_SIZE)) {
			final String firstKey = cache.keySet().iterator().next();
			cache.remove(firstKey);
		}
		serialize(instance);
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

	private static void serialize(final MomentTensorCache instance) {
		if (instance != null) {
			final File file = new File(EarthquakeBulletinConfig.MT_CACHE_FILE);
			file.getParentFile().mkdirs();
			try (final FileOutputStream fos = new FileOutputStream(file); final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
				logger.log(Level.CONFIG, "Serializing {0} into \"{1}\"...", new Serializable[] { instance, file });
				oos.writeObject(instance);
				logger.log(Level.CONFIG, "{0} serialized successfully.", instance);
			}
			catch (final IOException e) {
				logger.log(Level.WARNING, "Cannot serialize " + instance + ':', e);
			}
		}
	}

	private static MomentTensorCache deserialize() {
		final File file = new File(EarthquakeBulletinConfig.MT_CACHE_FILE);
		if (file.isFile()) {
			try (final FileInputStream fis = new FileInputStream(file); final ObjectInputStream ois = new ObjectInputStream(fis)) {
				logger.log(Level.CONFIG, "Deserializing {0} from \"{1}\"...", new Serializable[] { MomentTensorCache.class, file });
				final MomentTensorCache deserialized = (MomentTensorCache) ois.readObject();
				logger.log(Level.CONFIG, "{0} deserialized successfully.", deserialized);
				return deserialized;
			}
			catch (final IOException | ClassNotFoundException e) {
				logger.log(Level.WARNING, "Cannot deserialize " + MomentTensorCache.class + ':', e);
			}
		}
		return null;
	}

}
