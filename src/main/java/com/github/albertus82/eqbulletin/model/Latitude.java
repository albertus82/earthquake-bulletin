package com.github.albertus82.eqbulletin.model;

import static it.albertus.jface.maps.CoordinateUtils.DEGREE_SIGN;

import java.text.NumberFormat;

public class Latitude extends Coordinate {

	private static final long serialVersionUID = -5718184537510614995L;

	protected Latitude(final float value) {
		super(value);
	}

	public static Latitude valueOf(final float value) {
		return new Latitude(value);
	}

	@Override
	public String toString(final NumberFormat numberFormat) {
		return numberFormat.format(Math.abs(value)) + DEGREE_SIGN + (value < 0 ? 'S' : 'N');
	}

}
