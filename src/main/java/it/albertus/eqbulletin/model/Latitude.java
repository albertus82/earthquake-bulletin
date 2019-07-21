package it.albertus.eqbulletin.model;

import static it.albertus.jface.maps.CoordinateUtils.DEGREE_SIGN;

import it.albertus.jface.maps.CoordinateUtils;

public class Latitude extends Coordinate {

	private static final long serialVersionUID = -3229264319710892255L;

	protected Latitude(final float value) {
		super(value);
	}

	public static Latitude valueOf(final float value) {
		return new Latitude(value);
	}

	@Override
	public String toString() {
		return CoordinateUtils.getFormatter().format(Math.abs(value)) + DEGREE_SIGN + (value < 0 ? 'S' : 'N');
	}

}
