package it.albertus.earthquake.model;

public class Latitude extends Coordinate {

	private static final long serialVersionUID = -7954277797187606026L;

	public Latitude(final float value) {
		super(value);
	}

	@Override
	public String toString() {
		return numberFormats.get().format(Math.abs(value)) + '\u00B0' + (value > 0 ? 'N' : 'S');
	}

}
