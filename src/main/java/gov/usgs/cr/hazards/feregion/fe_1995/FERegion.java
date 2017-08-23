package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.util.WordUtils;
import it.albertus.util.logging.LoggerFactory;

/**
 * @author Bob Simpson
 */
public class FERegion {

	private static final Logger logger = LoggerFactory.getLogger(FERegion.class);

	private final FECache cache;

	/**
	 * Initializes a internal cache. Reuse the instance whenever possible.
	 */
	public FERegion() throws IOException {
		cache = new FECache();
	}

	public int getNumber(double lng, final double lat) {
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
		final String quad;
		if (lat >= 0.0) {
			if (lng >= 0.0) {
				quad = "ne";
			}
			else {
				quad = "nw";
			}
		}
		else {
			if (lng >= 0.0) {
				quad = "se";
			}
			else {
				quad = "sw";
			}
		}
		logger.log(Level.FINE, " * quad, lt, ln  = {0} {1} {2}", new Object[] { quad, lt, ln });

		// Find location of the latitude tier in the appropriate quadrant file.
		final int beg = cache.getLatbegins().get(quad).get(lt); // Location of first item for latitude lt.
		final int num = cache.getLonsperlat().get(quad).get(lt); // Number of items for latitude lt.
		logger.log(Level.FINE, " * beg = {0} num = {1}", new int[] { beg, num });

		// Extract this tier of longitude and f-e numbers for latitude lt.
		final List<Integer> mylons = cache.getLons().get(quad).subList(beg, beg + num);
		final List<Integer> myfenums = cache.getFenums().get(quad).subList(beg, beg + num);
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
		logger.log(Level.FINE, "{0} {1} {2}", new int[] { n, feindex, fenum });

		return fenum;
	}

	public String getName(final double lng, final double lat, final boolean prettyPrint) {
		final int fenum = getNumber(lng, lat);
		final String fename = cache.getNames().get(fenum - 1);
		logger.log(Level.FINE, "{0} {1}", new Object[] { fenum, fename });

		if (prettyPrint) {
			return WordUtils.capitalize(fename.toLowerCase(), ' ', '-', '.').replace(" Of ", " of "); // Improved text case.
		}
		else {
			return fename;
		}
	}

	/**
	 * Returns Flinn-Engdahl Region name from decimal lon,lat values given on
	 * command line.
	 * 
	 * Version 0.2 - Bob Simpson January, 2003 <simpson@usgs.gov>
	 * 
	 * With fix supplied by George Randall <ger@geophysics.lanl.gov> 2003-02-03
	 */
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

		final String fename = new FERegion().getName(lng, lat, false);

		System.out.println(fename);
	}

}
