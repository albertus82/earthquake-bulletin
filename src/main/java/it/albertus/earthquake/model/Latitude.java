package it.albertus.earthquake.model;

public class Latitude extends FloatWrapper {

	private static final long serialVersionUID = 2434704076425121073L;

	public Latitude(final float value) {
		super(value);
	}

	@Override
	public String toString() {
		return Float.toString(Math.abs(value)) + '\u00B0' + (value > 0 ? 'N' : 'S');
	}

}
