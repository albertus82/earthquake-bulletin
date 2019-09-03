package it.albertus.eqbulletin.model;

import static it.albertus.jface.maps.CoordinateUtils.DEGREE_SIGN;

import java.text.NumberFormat;

public class Longitude extends Coordinate {

	private static final long serialVersionUID = -8260226111354686900L;

	protected Longitude(final float value) {
		super(value);
	}

	public static Longitude valueOf(final float value) {
		return new Longitude(value);
	}

	@Override
	public String toString(final NumberFormat numberFormat) {
		return numberFormat.format(Math.abs(value)) + DEGREE_SIGN + (value < 0 ? 'W' : 'E');
	}

}
