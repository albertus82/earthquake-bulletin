package it.albertus.eqbulletin.model;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class Coordinate extends FloatValue {

	private static final long serialVersionUID = 7312923076881011764L;

	static final char DEGREE_SIGN = '\u00B0';

	public Coordinate(final float value) {
		super(value);
	}

	protected static final ThreadLocal<NumberFormat> numberFormats = new ThreadLocal<NumberFormat>() {
		@Override
		protected NumberFormat initialValue() {
			final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
			nf.setMinimumFractionDigits(2);
			nf.setMaximumFractionDigits(2);
			return nf;
		}
	};

}
