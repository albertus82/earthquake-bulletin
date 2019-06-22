package it.albertus.eqbulletin.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Depth implements Serializable, Comparable<Depth> {

	private static final long serialVersionUID = 2766555831235385141L;

	private static final LinkedHashMap<Short, Depth> cache = new LinkedHashMap<Short, Depth>(16, 0.75f, true) { // Flyweight
		private static final long serialVersionUID = -3656824180998473886L;

		private static final int MAX_ENTRIES = 1000;

		@Override
		protected boolean removeEldestEntry(final Entry<Short, Depth> eldest) {
			return size() > MAX_ENTRIES;
		}
	};

	private final short value; // Earth radius is the distance from Earth's center to its surface, about 6371 km (3959 mi).

	private Depth(final short value) {
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
