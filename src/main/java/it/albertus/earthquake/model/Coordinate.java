package it.albertus.earthquake.model;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class Coordinate extends FloatValue {

	private static final long serialVersionUID = 8835516568988478846L;

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
