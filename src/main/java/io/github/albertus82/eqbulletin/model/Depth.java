package io.github.albertus82.eqbulletin.model;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Value
public class Depth implements Comparable<Depth> {

	private static final int EARTH_RADIUS_METERS = 6_371_008;

	private static final LinkedHashMap<Short, Depth> cache = new LinkedHashMap<Short, Depth>(16, 0.75f, true) {

		private static final long serialVersionUID = -6085284206246682656L;

		private static final short MAX_ENTRIES = 0x3FF;

		@Override
		protected boolean removeEldestEntry(final Entry<Short, Depth> eldest) {
			final int size = size();
			log.trace("Depth cache size: {}.", size);
			return size > MAX_ENTRIES;
		}
	};

	private final short value; // Earth radius is about 6371 km (3959 mi).

	private Depth(final short value) {
		this.value = value;
		cache.put(value, this);
	}

	public static Depth valueOf(final int km) {
		if (km < 0 || km > EARTH_RADIUS_METERS / 1000) {
			throw new IllegalArgumentException(Integer.toString(km));
		}
		final Depth cached = cache.get((short) km);
		return cached != null ? cached : new Depth((short) km);
	}

	@Override
	public String toString() {
		return Short.toString(value) + " km";
	}

	@Override
	public int compareTo(final Depth o) {
		return Short.compare(this.value, o.value);
	}

}
