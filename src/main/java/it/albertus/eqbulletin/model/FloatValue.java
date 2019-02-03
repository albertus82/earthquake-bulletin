package it.albertus.eqbulletin.model;

abstract class FloatValue extends Number implements Comparable<FloatValue> {

	private static final long serialVersionUID = 2280288073514757941L;

	final float value;

	FloatValue(final float value) {
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
		if (!(obj instanceof FloatValue)) {
			return false;
		}
		final FloatValue other = (FloatValue) obj;
		return Float.floatToIntBits(value) == Float.floatToIntBits(other.value);
	}

	@Override
	public int compareTo(final FloatValue o) {
		return Float.compare(this.value, o.value);
	}

	@Override
	public int intValue() {
		return (int) value;
	}

	@Override
	public long longValue() {
		return (long) value;
	}

	@Override
	public float floatValue() {
		return value;
	}

	@Override
	public double doubleValue() {
		return Double.parseDouble(Float.toString(value));
	}

}
