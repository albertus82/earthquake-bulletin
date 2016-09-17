package it.albertus.earthquake.model;

public class Longitude extends FloatWrapper {

	private static final long serialVersionUID = 4555084498428295118L;

	public Longitude(final float value) {
		super(value);
	}

	@Override
	public String toString() {
		return Float.toString(Math.abs(value)) + '\u00B0' + (value > 0 ? 'E' : 'W');
	}

}
