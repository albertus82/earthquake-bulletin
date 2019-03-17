package it.albertus.eqbulletin.model;

import static it.albertus.jface.maps.CoordinateUtils.DEGREE_SIGN;

import it.albertus.jface.maps.CoordinateUtils;

public class Latitude extends Coordinate {

	private static final long serialVersionUID = -1261995592295338607L;

	public Latitude(final float value) {
		super(value);
	}

	@Override
	public String toString() {
		return CoordinateUtils.getFormatter().format(Math.abs(value)) + DEGREE_SIGN + (value < 0 ? 'S' : 'N');
	}

}
