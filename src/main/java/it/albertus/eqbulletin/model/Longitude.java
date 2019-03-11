package it.albertus.eqbulletin.model;

import it.albertus.jface.maps.CoordinateUtils;

public class Longitude extends Coordinate {

	private static final long serialVersionUID = -1729941517478220543L;

	public Longitude(final float value) {
		super(value);
	}

	@Override
	public String toString() {
		return CoordinateUtils.getFormatter().format(Math.abs(value)) + CoordinateUtils.DEGREE_SIGN + (value < 0 ? 'W' : 'E');
	}

}
