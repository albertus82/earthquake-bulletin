package it.albertus.eqbulletin.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = false)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class Coordinate extends Number implements FormattedNumber, Comparable<Coordinate> {

	private static final long serialVersionUID = 7850722288973835763L;

	final float value;

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

	@Override
	public String toString() {
		return Float.toString(value);
	}

}
