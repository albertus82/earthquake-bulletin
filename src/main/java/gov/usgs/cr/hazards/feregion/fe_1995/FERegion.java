package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("   Usage:  FERegion  <lon> <lat>");
			System.err.println("   As In:  FERegion  -122.5  36.2");
			System.err.println("   As In:  FERegion   122.5W 36.2N");
			System.exit(0);
		}
		String vlon = args[0];
		String vlat2 = args[1];

		// Allow for NSEW and switching of arguments.
		if (vlon.endsWith("N") || vlon.endsWith("S")) {
			String vtmp = vlon;
			vlon = vlat2;
			vlat2 = vtmp;
		}
		if (vlon.endsWith("W")) {
			vlon = "-" + vlon;
		}
		if (vlat2.endsWith("S")) {
			vlat2 = "-" + vlat2;
		}
		vlon = vlon.replaceAll("E|W", "");
		vlat2 = vlat2.replaceAll("N|S", "");

		// Adjust lat-lon values...
		double vlng = Double.parseDouble(vlon);
		double vlat = Double.parseDouble(vlat2);

		String vfename = getName(vlng, vlat);

		System.out.println(vfename);
	}

	public static String getName(double vlng, double vlat) throws IOException {
		if (vlng <= -180.0) {
			vlng += 360.0;
		}
		if (vlng > 180.0) {
			vlng -= 360.0;
		}

		// Take absolute values...
		double valat = Math.abs(vlat);
		double valon = Math.abs(vlng);
		if (valat > 90.0 || valon > 180.0) {
			System.err.printf(" * bad latitude or longitude: %f %f", vlat, vlng);
			System.err.println();
			System.exit(1);
		}

		// Truncate absolute values to integers...
		int vlt = (int) valat;
		int vln = (int) valon;

		// Get quadrant
		final String vquad;
		if (vlat >= 0.0) {
			if (vlng >= 0.0) {
				vquad = "ne";
			}
			else {
				vquad = "nw";
			}
		}
		else {
			if (vlng >= 0.0) {
				vquad = "se";
			}
			else {
				vquad = "sw";
			}
		}

		// Names of files containing Flinn-Engdahl Regionalization info.
		String vnames = "names.asc";
		String vquadsindex = "quadsidx.asc";
		String[] aquadorder = new String[] { "ne", "nw", "se", "sw" };
		String[] asectfiles = new String[] { "nesect.asc", "nwsect.asc", "sesect.asc", "swsect.asc" };

		// Read the file of region names...
		String[] anames = null;
		List<String> lnames = new ArrayList<>();
		try (final InputStream is = FERegion.class.getResourceAsStream(vnames); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				lnames.add(line.trim());
			}
			anames = lnames.toArray(new String[lnames.size()]);
		}

		// The quadsindex file contains a list for all 4 quadrants of the number of longitude entries for each integer latitude in the "sectfiles".
		int[] aquadsindex = null;
		List<Integer> lquadsindex = new ArrayList<>();
		try (final InputStream is = FERegion.class.getResourceAsStream(vquadsindex); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				for (String s : line.trim().split("\\s+")) {
					lquadsindex.add(Integer.valueOf(s));
				}
			}
			aquadsindex = new int[lquadsindex.size()];
			for (int i = 0; i < aquadsindex.length; i++) {
				aquadsindex[i] = lquadsindex.get(i);
			}
		}
		Map<String, int[]> mlonsperlat = new LinkedHashMap<>();
		Map<String, int[]> mlatbegins = new LinkedHashMap<>();

		Map<String, int[]> mlons = new LinkedHashMap<>();
		Map<String, int[]> mfenums = new LinkedHashMap<>();

		for (int i = 0; i < aquadorder.length; i++) {
			String vquad2 = aquadorder[i];
			// Break the quadindex array into 4 arrays, one for each quadrant.
			final int[] aquadindex = Arrays.copyOfRange(aquadsindex, 91 * i, 91 * (i + 1));
			mlonsperlat.put(vquad2, aquadindex);

			// Convert the lonsperlat array, which counts how many longitude items there are for each latitude,
			// into an array that tells the location of the beginning item in a quadrant's latitude stripe.
			int begin = 0;
			int end = -1;
			int n = 0;
			List<Integer> lbegins = new ArrayList<>();
			for (int item : aquadindex) {
				n++;
				begin = end + 1;
				lbegins.add(begin);
				end += item;
				if (n <= 10) {
					// System.out.printf("%s %d %d %d%s",vquad2, item, begin, end, System.lineSeparator());
				}
			}
			int[] abegins = new int[lbegins.size()];
			for (int j = 0; j < abegins.length; j++) {
				abegins[j] = lbegins.get(j);
			}
			mlatbegins.put(vquad2, abegins);

			String vsectfile = asectfiles[i];
			int[] asect = null;
			List<Integer> lsect = new ArrayList<>();
			try (final InputStream is = FERegion.class.getResourceAsStream(vsectfile); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
				String line;
				while ((line = br.readLine()) != null) {
					for (String s : line.trim().split("\\s+")) {
						lsect.add(Integer.valueOf(s));
					}
				}
				asect = new int[lsect.size()];
				for (int j = 0; j < asect.length; j++) {
					asect[j] = lsect.get(j);
				}
			}

			List<Integer> llons = new ArrayList<>();
			List<Integer> lfenums = new ArrayList<>();
			int[] alons = null;
			int[] afenums = null;
			int o = 0;
			for (int item : asect) { // Split pairs of items into two separate arrays:
				o++;
				if (o % 2 != 0) {
					llons.add(item);
				}
				else {
					lfenums.add(item);
				}
			}
			alons = new int[llons.size()];
			for (int j = 0; j < alons.length; j++) {
				alons[j] = llons.get(j);
			}
			mlons.put(vquad2, alons);

			afenums = new int[lfenums.size()];
			for (int j = 0; j < afenums.length; j++) {
				afenums[j] = lfenums.get(j);
			}
			mfenums.put(vquad2, afenums);
		}

		// Find location of the latitude tier in the appropriate quadrant file.
		int beg = mlatbegins.get(vquad)[vlt]; // Location of first item for latitude lt.
		int num = mlonsperlat.get(vquad)[vlt]; // Number of items for latitude lt.

		// Extract this tier of longitude and f-e numbers for latitude lt.
		int[] amylons = Arrays.copyOfRange(mlons.get(vquad), beg, beg + num);
		int[] amyfenums = Arrays.copyOfRange(mfenums.get(vquad), beg, beg + num);

		int n = 0;
		for (int item : amylons) {
			if (item > vln) {
				break;
			}
			n++;
		}

		int vfeindex = n - 1;
		int vfenum = amyfenums[vfeindex];
		return anames[vfenum - 1];
	}

}
