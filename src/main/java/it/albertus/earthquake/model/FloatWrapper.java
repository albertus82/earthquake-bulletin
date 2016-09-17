package it.albertus.earthquake.model;

import java.io.Serializable;

public class FloatWrapper implements Serializable, Comparable<FloatWrapper> {

	private static final long serialVersionUID = 3280288073514757941L;

	protected final float value;

	public FloatWrapper(final float value) {
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(value);
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
		if (!(obj instanceof FloatWrapper)) {
			return false;
		}
		FloatWrapper other = (FloatWrapper) obj;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final FloatWrapper o) {
		return Float.compare(this.value, o.value);
	}

}
