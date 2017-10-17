package it.albertus.eqbulletin.model;

import java.io.Serializable;

public class Depth implements Serializable, Comparable<Depth> {

	private static final long serialVersionUID = 895253066207604466L;

	private final int value;

	public Depth(final int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return Integer.toString(value) + " km";
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
		Depth other = (Depth) obj;
		if (value != other.value) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final Depth o) {
		return Integer.compare(this.value, o.value);
	}

}
