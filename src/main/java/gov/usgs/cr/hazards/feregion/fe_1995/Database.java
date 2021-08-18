package gov.usgs.cr.hazards.feregion.fe_1995;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * This class represents the Flinn-Engdahl regions database used by
 * {@link FERegion} to retrieve region informations.
 * <p>
 * Originally written by Bob Simpson in Perl language, with fix supplied by
 * George Randall (<tt>feregion.pl</tt>).
 * 
 * @see {@link FERegion}
 * @see <a href="ftp://hazards.cr.usgs.gov/feregion/fe_1995/">1995 (latest)
 *      revision of the Flinn-Engdahl (F-E) seismic and geographical
 *      regionalization scheme and programs</a>
 */
@Slf4j
class Database {

	// Names of files containing Flinn-Engdahl Regionalization info.
	private static final String[] sectfiles = { "nesect.asc", "nwsect.asc", "sesect.asc", "swsect.asc" };
	private static final String[] quadorder = { "ne", "nw", "se", "sw" };

	private final List<String> names = new ArrayList<>(757);

	private final Map<String, List<Integer>> lonsperlat = new HashMap<>(quadorder.length * 2);
	private final Map<String, List<Integer>> latbegins = new HashMap<>(quadorder.length * 2);

	private final Map<String, List<Integer>> mlons = new HashMap<>(quadorder.length * 2);
	private final Map<String, List<Integer>> mfenums = new HashMap<>(quadorder.length * 2);

	private final List<Integer> seisreg = new ArrayList<>(757);

	Database() throws IOException { // NOSONAR Preserve comparability with Perl source.
		final long startTime = System.nanoTime();

		final Pattern pattern = Pattern.compile("\\s+");

		// Read the file of region names...
		try (final InputStream is = getClass().getResourceAsStream("names.asc"); final InputStreamReader isr = new InputStreamReader(is, US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				names.add(line.trim());
			}
		}

		// The quadsindex file contains a list for all 4 quadrants of the number of longitude entries for each integer latitude in the "sectfiles".
		final List<Integer> quadsindex = new ArrayList<>(91 * quadorder.length);
		try (final InputStream is = getClass().getResourceAsStream("quadsidx.asc"); final InputStreamReader isr = new InputStreamReader(is, US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				for (final String index : pattern.split(line.trim())) {
					quadsindex.add(Integer.valueOf(index));
				}
			}
		}
		log.debug("  * Numitems in quadsindex = {}", quadsindex.size());

		final Map<String, List<Integer>> sects = new HashMap<>(quadorder.length * 2);
		for (int i = 0; i < sectfiles.length; i++) {
			final List<Integer> sect = new ArrayList<>(2666);
			try (final InputStream is = getClass().getResourceAsStream(sectfiles[i]); final InputStreamReader isr = new InputStreamReader(is, US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
				String line;
				while ((line = br.readLine()) != null) {
					for (final String s : pattern.split(line.trim())) {
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
				if (log.isDebugEnabled() && n <= 10) {
					log.debug("{} {} {} {}", quad, item, begin, end);
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

		// mksrtb.for
		try (final InputStream is = getClass().getResourceAsStream("seisrdef.asc"); final InputStreamReader isr = new InputStreamReader(is, US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				final String[] row = pattern.split(line.trim());
				final Integer value = Integer.valueOf(row[0]);
				final short from = Short.parseShort(row[1]);
				final short to = row.length > 2 ? Short.parseShort(row[2]) : from;
				for (short i = from; i <= to; i++) {
					seisreg.add(value);
				}
			}
		}

		log.debug("F-E regions database initialized in {} ns.", System.nanoTime() - startTime);
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

	List<Integer> getSeisreg() {
		return seisreg;
	}

}
