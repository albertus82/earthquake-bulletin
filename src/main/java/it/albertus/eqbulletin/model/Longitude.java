package it.albertus.eqbulletin.model;

public class Longitude extends Coordinate {

	private static final long serialVersionUID = -5019487613256405246L;

	public Longitude(final float value) {
		super(value);
	}

	@Override
	public String toString() {
		return numberFormats.get().format(Math.abs(value)) + DEGREE_SIGN + (value < 0 ? 'W' : 'E');
	}

}
