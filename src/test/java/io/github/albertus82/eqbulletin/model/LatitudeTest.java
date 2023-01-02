package io.github.albertus82.eqbulletin.model;

import static org.junit.jupiter.api.Assertions.*;

import java.text.NumberFormat;
import java.util.Locale;

import org.junit.jupiter.api.Test;

class LatitudeTest {

	@Test
	public void testToString_withPositiveValue() {
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		Latitude obj = new Latitude(50.123456f);
		String expected = "50.123\u00B0N";
		String result = obj.toString(numberFormat);
		assertEquals(expected, result);
	}

	@Test
	public void testToString_withNegativeValue() {
		NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
		Latitude obj = new Latitude(-50.123456f);
		String expected = "50.123\u00B0S";
		String result = obj.toString(numberFormat);
		assertEquals(expected, result);
	}

}
