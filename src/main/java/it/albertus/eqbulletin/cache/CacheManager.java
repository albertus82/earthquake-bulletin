package it.albertus.eqbulletin.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.util.logging.LoggerFactory;

public class CacheManager<T extends Cache<?, ?>> {

	static final String CACHE_DIRECTORY = EarthquakeBulletinConfig.APPDATA_DIRECTORY + File.separator + "cache";

	private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

	void serialize(final T instance, final String pathname) {
		if (instance != null) {
			final File file = new File(pathname);
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

	T deserialize(final String pathname) {
		final File file = new File(pathname);
		if (file.isFile()) {
			try (final FileInputStream fis = new FileInputStream(file); final ObjectInputStream ois = new ObjectInputStream(fis)) {
				logger.log(Level.CONFIG, "Deserializing {0} from \"{1}\"...", new Serializable[] { MapImageCache.class, file });
				final Object readObject = ois.readObject();
				@SuppressWarnings("unchecked")
				final T deserialized = (T) readObject;
				logger.log(Level.CONFIG, "{0} deserialized successfully.", deserialized);
				return deserialized;
			}
			catch (final IOException | ClassNotFoundException e) {
				logger.log(Level.WARNING, "Cannot deserialize " + MapImageCache.class + ':', e);
			}
		}
		return null;
	}

	void delete(final String pathname) {
		final Path path = Paths.get(pathname);
		try {
			if (Files.deleteIfExists(path)) {
				logger.log(Level.CONFIG, "Deleted cache file \"{0}\".", path);
			}
		}
		catch (final IOException e) {
			logger.log(Level.WARNING, "Cannot delete cache file \"{0}\".", path);
		}
	}

}
