package it.albertus.eqbulletin.model;

import static it.albertus.jface.maps.CoordinateUtils.DEGREE_SIGN;

import it.albertus.jface.maps.CoordinateUtils;

public class Longitude extends Coordinate {

	private static final long serialVersionUID = -1880226620057493276L;

	protected Longitude(final float value) {
		super(value);
	}

	public static Longitude valueOf(final float value) {
		return new Longitude(value);
	}

	@Override
	public String toString() {
		return CoordinateUtils.getFormatter().format(Math.abs(value)) + DEGREE_SIGN + (value < 0 ? 'W' : 'E');
	}

}
