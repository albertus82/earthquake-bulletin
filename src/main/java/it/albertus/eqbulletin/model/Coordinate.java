package it.albertus.eqbulletin.model;

abstract class Coordinate extends Number implements Comparable<Coordinate> {

	private static final long serialVersionUID = -4028527688806190212L;

	final float value;

	Coordinate(final float value) {
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
		if (!(obj instanceof Coordinate)) {
			return false;
		}
		final Coordinate other = (Coordinate) obj;
		return Float.floatToIntBits(value) == Float.floatToIntBits(other.value);
	}

	@Override
	public int compareTo(final Coordinate o) {
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
