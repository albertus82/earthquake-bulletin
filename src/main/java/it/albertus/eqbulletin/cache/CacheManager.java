package it.albertus.eqbulletin.cache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
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
			try (final OutputStream fos = new FileOutputStream(file); final OutputStream bos = new BufferedOutputStream(fos); final ObjectOutput oo = new ObjectOutputStream(bos)) {
				logger.log(Level.CONFIG, "Serializing {0} to \"{1}\"...", new Serializable[] { instance, file });
				oo.writeObject(instance);
				logger.log(Level.CONFIG, "{0} serialized successfully.", instance);
			}
			catch (final IOException e) {
				logger.log(Level.WARNING, e, () -> "Cannot serialize " + instance + ':');
			}
		}
	}

	T deserialize(final String pathname, final Class<T> clazz) {
		final File file = new File(pathname);
		if (file.isFile()) {
			try (final InputStream fis = new FileInputStream(file); final InputStream bis = new BufferedInputStream(fis); final ObjectInput oi = new LookAheadObjectInputStream(bis, clazz)) {
				logger.log(Level.CONFIG, "Deserializing cache object from \"{0}\"...", file);
				@SuppressWarnings("unchecked")
				final T deserialized = (T) oi.readObject();
				if (clazz.isInstance(deserialized)) {
					logger.log(Level.CONFIG, "{0} deserialized successfully.", deserialized);
					return deserialized;
				}
				else {
					throw new ClassCastException(deserialized.getClass().getName() + " cannot be cast to " + clazz.getName());
				}
			}
			catch (final IOException | ClassNotFoundException | ClassCastException e) {
				logger.log(Level.WARNING, e, () -> "Cannot deserialize cache object from \"" + file + "\":");
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
			logger.log(Level.WARNING, e, () -> "Cannot delete cache file \"" + path + "\":");
		}
	}

	private class LookAheadObjectInputStream extends ObjectInputStream {

		private final Class<T> clazz;
		private boolean first = true;

		private LookAheadObjectInputStream(final InputStream in, final Class<T> clazz) throws IOException {
			super(in);
			Objects.requireNonNull(clazz);
			this.clazz = clazz;
		}

		@Override
		protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			if (first) {
				if (clazz.getName().equals(desc.getName())) {
					logger.log(Level.FINE, "Deserialization allowed: {0}", desc);
					first = false;
				}
				else {
					throw new InvalidClassException(desc.getName());
				}
			}
			return super.resolveClass(desc);
		}
	}

}
