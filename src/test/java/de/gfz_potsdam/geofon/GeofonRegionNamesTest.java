package de.gfz_potsdam.geofon;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GeofonRegionNamesTest {

	private static GeofonRegionNames grnames;

	@BeforeAll
	static void init() throws IOException {
		grnames = new GeofonRegionNames();
	}

	@Test
	void test() {
		final Map<Integer, String> names = grnames.getMap();
		Assertions.assertEquals(757, names.size());
		for (final Entry<Integer, String> entry : names.entrySet()) {
			Assertions.assertNotNull(entry.getValue());
			Assertions.assertNotEquals("", entry.getValue());
		}
	}

}
