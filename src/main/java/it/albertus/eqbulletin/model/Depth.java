package it.albertus.eqbulletin.model;

import java.io.Serializable;

public class Depth implements Serializable, Comparable<Depth> {

	private static final long serialVersionUID = -8317438372695311838L;

	private final short value; // Earth radius is the distance from Earth's center to its surface, about 6,371 km (3,959 mi).

	public Depth(final short value) {
		this.value = value;
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
