package it.albertus.earthquake.model;

public class Longitude extends Coordinate {

	private static final long serialVersionUID = -5019487613256405246L;

	public Longitude(final float value) {
		super(value);
	}

	@Override
	public String toString() {
		return numberFormats.get().format(Math.abs(value)) + '\u00B0' + (value > 0 ? 'E' : 'W');
	}

}
