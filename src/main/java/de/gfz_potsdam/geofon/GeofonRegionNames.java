package de.gfz_potsdam.geofon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.TreeMap;

public class GeofonRegionNames {

	private final Map<Integer, String> names = new TreeMap<>();

	/**
	 * Initializes the internal names database. Reuse the same instance whenever
	 * possible.
	 */
	public GeofonRegionNames() throws IOException {
		// Read the file of region names...
		try (final InputStream is = getClass().getResourceAsStream("names.asc"); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			int i = 1;
			String line;
			while ((line = br.readLine()) != null) {
				names.put(i++, line.trim());
			}
		}
	}

	public Map<Integer, String> getNames() {
		return names;
	}

}
