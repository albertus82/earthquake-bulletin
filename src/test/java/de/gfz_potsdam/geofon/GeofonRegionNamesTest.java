package de.gfz_potsdam.geofon;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GeofonRegionNamesTest {

	private static GeofonRegionNames grnames;

	@BeforeClass
	public static void init() throws IOException {
		grnames = new GeofonRegionNames();
	}

	@Test
	public void test() {
		final Map<Integer, String> names = grnames.getNames();
		Assert.assertEquals(757, names.size());
		for (final Entry<Integer, String> entry : names.entrySet()) {
			Assert.assertNotNull(entry.getValue());
			Assert.assertNotEquals("", entry.getValue());
		}
	}

}
