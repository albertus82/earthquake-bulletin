package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.util.logging.LoggerFactory;

/**
 * @author Bob Simpson
 */
class Database implements Serializable {

	private static final long serialVersionUID = -3690479189168351433L;

	private static final Logger logger = LoggerFactory.getLogger(Database.class);

	// Names of files containing Flinn-Engdahl Regionalization info.
	private static final String[] sectfiles = { "nesect.asc", "nwsect.asc", "sesect.asc", "swsect.asc" };
	private static final String[] quadorder = { "ne", "nw", "se", "sw" };

	private final List<String> names = new ArrayList<>(757);

	private final Map<String, List<Integer>> lonsperlat = new HashMap<>(quadorder.length);
	private final Map<String, List<Integer>> latbegins = new HashMap<>(quadorder.length);

	private final Map<String, List<Integer>> mlons = new HashMap<>(quadorder.length);
	private final Map<String, List<Integer>> mfenums = new HashMap<>(quadorder.length);

	Database() throws IOException {
		// Read the file of region names...
		try (final InputStream is = getClass().getResourceAsStream("names.asc"); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				names.add(line.trim());
			}
		}

		// The quadsindex file contains a list for all 4 quadrants of the number of longitude entries for each integer latitude in the "sectfiles".
		final List<Integer> quadsindex = new ArrayList<>(91 * quadorder.length);
		try (final InputStream is = getClass().getResourceAsStream("quadsidx.asc"); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				for (final String index : line.trim().split("\\s+")) {
					quadsindex.add(Integer.valueOf(index));
				}
			}
		}
		logger.log(Level.FINE, "  * Numitems in quadsindex = {0}", quadsindex.size());

		final Map<String, List<Integer>> sects = new HashMap<>(quadorder.length);
		for (int i = 0; i < sectfiles.length; i++) {
			final List<Integer> sect = new ArrayList<>(2000);
			try (final InputStream is = getClass().getResourceAsStream(sectfiles[i]); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
				String line;
				while ((line = br.readLine()) != null) {
					for (final String s : line.trim().split("\\s+")) {
						sect.add(Integer.valueOf(s));
					}
				}
				sects.put(quadorder[i], sect);
			}
		}

		for (int i = 0; i < quadorder.length; i++) {
			final String quad = quadorder[i];

			// Break the quadindex array into 4 arrays, one for each quadrant.
			final List<Integer> quadindex = quadsindex.subList(91 * i, 91 * (i + 1));
			lonsperlat.put(quad, quadindex);

			// Convert the lonsperlat array, which counts how many longitude items there are for each latitude,
			// into an array that tells the location of the beginning item in a quadrant's latitude stripe.
			int begin = 0;
			int end = -1;
			int n = 0;
			final List<Integer> begins = new ArrayList<>(quadindex.size());
			for (final int item : quadindex) {
				n++;
				begin = end + 1;
				begins.add(begin);
				end += item;
				if (logger.isLoggable(Level.FINE) && n <= 10) {
					logger.log(Level.FINE, "{0} {1} {2} {3}", new Object[] { quad, item, begin, end });
				}
			}
			latbegins.put(quad, begins);

			final List<Integer> sect = sects.get(quad);
			final List<Integer> lons = new ArrayList<>(sect.size() / 2);
			final List<Integer> fenums = new ArrayList<>(sect.size() / 2);
			int o = 0;
			for (final int item : sect) { // Split pairs of items into two separate arrays:
				o++;
				if (o % 2 != 0) {
					lons.add(item);
				}
				else {
					fenums.add(item);
				}
			}
			mlons.put(quad, lons);
			mfenums.put(quad, fenums);
		}
	}

	List<String> getNames() {
		return names;
	}

	Map<String, List<Integer>> getLonsperlat() {
		return lonsperlat;
	}

	Map<String, List<Integer>> getLatbegins() {
		return latbegins;
	}

	Map<String, List<Integer>> getLons() {
		return mlons;
	}

	Map<String, List<Integer>> getFenums() {
		return mfenums;
	}

}
