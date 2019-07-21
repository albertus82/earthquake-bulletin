package it.albertus.eqbulletin.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.util.logging.LoggerFactory;

public class Depth implements Serializable, Comparable<Depth> {

	private static final long serialVersionUID = 894681925414643001L;

	private static final Logger logger = LoggerFactory.getLogger(Depth.class);

	private static final LinkedHashMap<Short, Depth> cache = new LinkedHashMap<Short, Depth>(16, 0.75f, true) { // Flyweight
		private static final long serialVersionUID = -3229317830656593292L;

		private static final short MAX_ENTRIES = 0xFF;

		@Override
		protected boolean removeEldestEntry(final Entry<Short, Depth> eldest) {
			final int size = size();
			logger.log(Level.FINER, "Depth cache size: {0}.", size);
			return size > MAX_ENTRIES;
		}
	};

	private final short value; // Earth radius is the distance from Earth's center to its surface, about 6371 km (3959 mi).

	protected Depth(final short value) {
		this.value = value;
		cache.put(value, this);
	}

	public static Depth valueOf(final short km) {
		final Depth cached = cache.get(km);
		return cached != null ? cached : new Depth(km);
	}

	@Override
	public String toString() {
		return Short.toString(value) + " km";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Depth)) {
			return false;
		}
		final Depth other = (Depth) obj;
		return value == other.value;
	}

	@Override
	public int compareTo(final Depth o) {
		return Short.compare(this.value, o.value);
	}

}
