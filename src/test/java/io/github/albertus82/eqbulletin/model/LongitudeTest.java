package io.github.albertus82.eqbulletin.model;

import static org.junit.jupiter.api.Assertions.*;

import java.text.NumberFormat;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class LongitudeTest {

	@Test
	public void testToString_withPositiveValue() {
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		Longitude obj = new Longitude(50.12f);
		String expected = "50.12\u00B0E";
		String result = obj.toString(numberFormat);
		assertEquals(expected, result);
	}

	@Test
	public void testToString_withNegativeValue() {
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		Longitude obj = new Longitude(-50.12f);
		String expected = "50.12\u00B0W";
		String result = obj.toString(numberFormat);
		assertEquals(expected, result);
	}

}
