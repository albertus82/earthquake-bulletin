package de.gfz_potsdam.geofon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import lombok.Getter;

@Getter
public class GeofonRegionNames {

	private final Map<Integer, String> map = new TreeMap<>();

	/**
	 * Initializes the internal names database. Reuse the same instance whenever
	 * possible.
	 */
	public GeofonRegionNames() throws IOException {
		// Read the file of region names...
		try (final InputStream is = getClass().getResourceAsStream("names.asc"); final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
			int fenum = 1;
			String line;
			while ((line = br.readLine()) != null) {
				map.put(fenum++, line.trim());
			}
		}
	}

}
