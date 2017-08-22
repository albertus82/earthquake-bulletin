package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.util.logging.LoggerFactory;

/**
 * Returns Flinn-Engdahl Region name from decimal lon,lat values given on
 * command line.
 * 
 * Version 0.2 - Bob Simpson January, 2003 <simpson@usgs.gov>
 * 
 * With fix supplied by George Randall <ger@geophysics.lanl.gov> 2003-02-03
 * 
 * @author Bob Simpson
 */
public class FERegion {

	private static final Logger logger = LoggerFactory.getLogger(FERegion.class);

	// Names of files containing Flinn-Engdahl Regionalization info.
	private static final String NAMES = "names.asc";
	private static final String QUADSINDEX = "quadsidx.asc";
	private static final String[] quadorder = { "ne", "nw", "se", "sw" };
	private static final String[] sectfiles = { "nesect.asc", "nwsect.asc", "sesect.asc", "swsect.asc" };

	public static void main(final String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("   Usage:  FERegion  <lon> <lat>");
			System.err.println("   As In:  FERegion  -122.5  36.2");
			System.err.println("   As In:  FERegion   122.5W 36.2N");
			System.exit(0);
		}
		String arg0 = args[0];
		String arg1 = args[1];

		// Allow for NSEW and switching of arguments.
		if (arg0.endsWith("N") || arg0.endsWith("S")) {
			final String tmp = arg0;
			arg0 = arg1;
			arg1 = tmp;
		}
		if (arg0.endsWith("W")) {
			arg0 = '-' + arg0;
		}
		if (arg1.endsWith("S")) {
			arg1 = '-' + arg1;
		}
		arg0 = arg0.replaceAll("E|W", "");
		arg1 = arg1.replaceAll("N|S", "");

		// Adjust lat-lon values...
		final double lng = Double.parseDouble(arg0);
		final double lat = Double.parseDouble(arg1);

		if (Math.abs(lat) > 90.0 || Math.abs(lng) > 180.0) {
			System.err.printf(" * bad latitude or longitude: %f %f", lat, lng);
			System.err.println();
			System.exit(1);
		}

		final String fename = getName(lng, lat);

		System.out.println(fename);
	}

	public static String getName(double lng, final double lat) throws IOException {
		if (lng <= -180.0) {
			lng += 360.0;
		}
		if (lng > 180.0) {
			lng -= 360.0;
		}

		// Take absolute values & truncate to integers...
		final int lt = (int) Math.abs(lat);
		final int ln = (int) Math.abs(lng);

		// Get quadrant
		final String myquad;
		if (lat >= 0.0) {
			if (lng >= 0.0) {
				myquad = "ne";
			}
			else {
				myquad = "nw";
			}
		}
		else {
			if (lng >= 0.0) {
				myquad = "se";
			}
			else {
				myquad = "sw";
			}
		}
		logger.log(Level.FINE, " * quad, lt, ln  = {0} {1} {2}", new Object[] { myquad, lt, ln });

		// Read the file of region names...
		final List<String> names = new ArrayList<>(757);
		try (final InputStream is = FERegion.class.getResourceAsStream(NAMES); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				names.add(line.trim());
				// System.out.println(WordUtils.capitalize(line.trim().toLowerCase(), '-', ' ').replace(" Of ", " of "));
			}
		}

		// The quadsindex file contains a list for all 4 quadrants of the number of longitude entries for each integer latitude in the "sectfiles".
		final List<Integer> quadsindex = new ArrayList<>(91 * quadorder.length);
		try (final InputStream is = FERegion.class.getResourceAsStream(QUADSINDEX); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				for (final String index : line.trim().split("\\s+")) {
					quadsindex.add(Integer.valueOf(index));
				}
			}
		}
		logger.log(Level.FINE, "  * Numitems in quadsindex = {0}", quadsindex.size());

		final Map<String, List<Integer>> lonsperlat = new HashMap<>(quadorder.length);
		final Map<String, List<Integer>> latbegins = new HashMap<>(quadorder.length);

		final Map<String, List<Integer>> mlons = new HashMap<>(quadorder.length);
		final Map<String, List<Integer>> mfenums = new HashMap<>(quadorder.length);

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

			final List<Integer> sect = new ArrayList<>(2000);
			try (final InputStream is = FERegion.class.getResourceAsStream(sectfiles[i]); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
				String line;
				while ((line = br.readLine()) != null) {
					for (final String s : line.trim().split("\\s+")) {
						sect.add(Integer.valueOf(s));
					}
				}
			}

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

		// Find location of the latitude tier in the appropriate quadrant file.
		final int beg = latbegins.get(myquad).get(lt); // Location of first item for latitude lt.
		final int num = lonsperlat.get(myquad).get(lt); // Number of items for latitude lt.
		logger.log(Level.FINE, " * beg = {0} num = {1}", new int[] { beg, num });

		// Extract this tier of longitude and f-e numbers for latitude lt.
		final List<Integer> mylons = mlons.get(myquad).subList(beg, beg + num);
		final List<Integer> myfenums = mfenums.get(myquad).subList(beg, beg + num);
		logger.log(Level.FINE, "mylons: {0}", mylons);
		logger.log(Level.FINE, "myfenums: {0}", myfenums);

		int n = 0;
		for (final int item : mylons) {
			if (item > ln) {
				break;
			}
			n++;
		}

		final int feindex = n - 1;
		final int fenum = myfenums.get(feindex);
		final String fename = names.get(fenum - 1);
		logger.log(Level.FINE, "{0} {1} {2} {3}", new Object[] { n, feindex, fenum, fename });

		return fename;
	}

}
