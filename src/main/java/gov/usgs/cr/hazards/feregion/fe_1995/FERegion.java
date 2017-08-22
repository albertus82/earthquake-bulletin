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

public class FERegion {

	public static void main(String[] args) throws IOException {
//		#!/usr/local/bin/perl -w
//
//		# feregion.pl - returns Flinn-Engdahl Region name from decimal lon,lat values given on command line.
//
//		# Version 0.2 - Bob Simpson January, 2003  <simpson@usgs.gov>
//		#               With fix supplied by George Randall <ger@geophysics.lanl.gov>  2003-02-03
//
//
//		($lon, $lat) = @ARGV;
//
//		if ( ! defined($lat) ) {
//		   print STDERR "   Usage:  feregion.pl  <lon> <lat>\n";
//		   print STDERR "   As In:  feregion.pl  -122.5  36.2\n";
//		   print STDERR "   As In:  feregion.pl   122.5W 36.2N\n";
//		   exit;
//		}
//
//		# Allow for NSEW and switching of arguments.
//		if ( $lon =~ /N|S/ ) { $tmp = $lon; $lon = $lat; $lat = $tmp; }
//		if ( $lon =~ /W/ ) { $lon =~ s/^/-/; }
//		if ( $lat =~ /S/ ) { $lat =~ s/^/-/; }
//		$lon =~ s/E|W//;
//		$lat =~ s/N|S//;
//
//		# Adjust lat-lon values...
//		$lng = $lon;
//		if ( $lng <= -180.0 )  { $lng += 360.0 }
//		if ( $lng >   180.0 )  { $lng -= 360.0 }
//
//		# Take absolute values...
//		$alat = abs($lat);
//		$alon = abs($lng);
//		if ($alat > 90.0 || $alon > 180.0) {
//		   print STDERR " * bad latitude or longitude: $lat $lng\n";
//		   exit;
//		}
//
//		# Truncate absolute values to integers...
//		$lt = int($alat);
//		$ln = int($alon);
		
		int vlt = 42;
		int vln = 12;
		
		double vlat =42.0;
	double vlng = 12.0;
//
//		# Get quadrant
//		if ($lat >= 0.0 && $lng >= 0.0) { $quad = "ne" }
//		if ($lat >= 0.0 && $lng <  0.0) { $quad = "nw" }
//		if ($lat <  0.0 && $lng >= 0.0) { $quad = "se" }
//		if ($lat <  0.0 && $lng <  0.0) { $quad = "sw" }
		
		final String vquad;
		if (vlat >= 0) {
			if (vlng >= 0) {
				vquad = "ne";
			}
			else {
				vquad = "nw";
			}
		}
		else {
			if (vlng >= 0) {
				vquad = "se";
			}
			else {
				vquad = "sw";
			}
		}

//
//		# print " * quad, lt, ln  = $quad $lt $ln\n";
//
//		# Names of files containing Flinn-Engdahl Regionalization info.
//		$names = "names.asc";
//		$quadsindex = "quadsidx.asc";
//		@quadorder = ("ne", "nw", "se", "sw");
//		@sectfiles = ("nesect.asc", "nwsect.asc", "sesect.asc", "swsect.asc");
		String vnames = "names.asc";
		String vquadsindex = "quadsidx.asc";
		String[] aquadorder = new String[] { "ne", "nw", "se", "sw" };
		String[] asectfiles = new String[] { "nesect.asc", "nwsect.asc", "sesect.asc", "swsect.asc" };
		
//		# Read the file of region names...
//		$NAMES = "<$names";
//		open NAMES  or  die " * Can't open $NAMES ... $!";
//		@names = ();
//		while (<NAMES>) {
//		   chomp;
//		   push @names, $_;
//		}
		String[] anames = null;
		List<String> lnames = new ArrayList<>();
		try (final InputStream is = FERegion.class.getResourceAsStream(vnames); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.trim().isEmpty()) {
					lnames.add(line);
				}
			}
			anames = lnames.toArray(new String[lnames.size()]);
		}
		 
//		System.out.println(Arrays.toString(anames));
		
//		# The quadsindex file contains a list for all 4 quadrants of the number of longitude entries for each integer latitude in the "sectfiles".
//		$QUADSINDEX = "<$quadsindex";
//		open QUADSINDEX  or  die " * Can't open $QUADSINDEX ... $!";
//		@quadsindex = ();
//		while(<QUADSINDEX>) {
//		   @newnums = split(" ",$_);
//		   push @quadsindex, @newnums;
//		}
//		close QUADSINDEX;
//		# $numitems = @quadsindex;
//		# print "  * Numitems in $quadsindex = $numitems\n";
		int[] aquadsindex = null;
		List<Integer> lquadsindex = new ArrayList<>();
		try (final InputStream is = FERegion.class.getResourceAsStream(vquadsindex); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
			String line;
			while ((line = br.readLine()) != null) {
				for (String s : line.split("\\s")) {
					if (!s.trim().isEmpty()) {
						lquadsindex.add(Integer.valueOf(s));
					}
				}
			}
			aquadsindex = new int[lquadsindex.size()];
			for (int i = 0; i < aquadsindex.length; i++) {
				aquadsindex[i] = lquadsindex.get(i);
			}
		}
//		System.out.println("  * Numitems in $quadsindex = " + aquadsindex.length);		
//		System.out.println(Arrays.toString(aquadsindex));
		
		
//		foreach $quad (@quadorder) {
		Map<String, int[]> mlonsperlat = new LinkedHashMap<>();
		Map<String, int[]> mlatbegins = new LinkedHashMap<>();
		
		Map<String, int[]> mlons = new LinkedHashMap<>();
		Map<String, int[]> mfenums = new LinkedHashMap<>();

		for (int i = 0; i < aquadorder.length; i++ ) {
			String vquad2 = aquadorder[i];
//			System.out.println(vquad2);
			
//		    # Break the quadindex array into 4 arrays, one for each quadrant.
//		    @quadindex = splice(@quadsindex,0,91);
//		    $lonsperlat{$quad} = [ @quadindex ];
			
			final int[] aquadindex = Arrays.copyOfRange(aquadsindex, 91 * i, 91 * (i + 1));
			mlonsperlat.put(vquad2, aquadindex);
//			System.out.println(Arrays.toString(copyOfRange));

//		    # Convert the lonsperlat array, which counts how many longitude items there are for each latitude,
//		    #   into an array that tells the location of the beginning item in a quadrant's latitude stripe.
//		    $begin = 0; $end = -1;
//		    @begins = ();
//		    $n = 0;
//		    foreach $item (@quadindex) {
//		       $n++;
//		       $begin = $end + 1;
//		       push @begins, $begin;
//		       $end += $item;
//		       # if ( $n <= 10 ) { print " $quad $item $begin $end\n"; }
//		    }
//		    $latbegins{$quad} = [ @begins ];
			int begin = 0;
			int end = -1;
			int n = 0;
			List<Integer> lbegins = new ArrayList<>();
			for (int item : aquadindex) {
				n++;
				begin = end+1;
				lbegins.add(begin);
				end += item;
				if ( n <= 10) {
//					System.out.printf("%s %d %d %d%s",vquad2, item, begin, end, System.lineSeparator());
				}
			}
			int[] abegins = new int[lbegins.size()];
			for (int j = 0; j < abegins.length; j++) {
				abegins[j] = lbegins.get(j);
			}
			mlatbegins.put(vquad2, abegins);
			
//		    $sectfile = shift @sectfiles;
//		    $SECTFILE = "<$sectfile";
//		    open SECTFILE  or  die " * Can't open $SECTFILE ... $!";
//
//		    @sect = ();
//		    while(<SECTFILE>) {
//		       @newnums = split(" ",$_);
//		       push @sect, @newnums;
//		    }
//		    close SECTFILE;
			
			String vsectfile = asectfiles[i];
			int[] asect = null;
			List<Integer> lsect = new ArrayList<>();
			try (final InputStream is = FERegion.class.getResourceAsStream(vsectfile); final InputStreamReader isr = new InputStreamReader(is); final BufferedReader br = new BufferedReader(isr)) {
				String line;
				while ((line = br.readLine()) != null) {
					for (String s : line.split("\\s")) {
						if (!s.trim().isEmpty()) {
							lsect.add(Integer.valueOf(s));
						}
					}
				}
				asect = new int[lsect.size()];
				for (int j = 0; j < asect.length; j++) {
					asect[j] = lsect.get(j);
				}
			}
//			System.out.println(Arrays.toString(asect));

//		    @lons = ();
//		    @fenums = ();
//		    $n = 0;
//		    foreach $item (@sect) {  # Split pairs of items into two separate arrays:
//		       $n++;
//		       if ( $n%2 ) { push @lons, $item; }
//		       else        { push @fenums, $item; }
//		    }
//		    $lons{$quad} = [ @lons ];
//		    $fenums{$quad} = [ @fenums ];
//
//		}
			List<Integer> llons = new ArrayList<>();
			List<Integer> lfenums = new ArrayList<>();
			int[] alons = null;
			int[] afenums = null;
			int o = 0;
			for (int item : asect) {
				o++;
				if (o%2!=0) {
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
			
//			System.out.println(Arrays.toString(alons));System.out.println();
//			System.out.println(Arrays.toString(afenums));System.out.println();
		}

//		# Find location of the latitude tier in the appropriate quadrant file.
//		$beg =  @{$latbegins{$quad}}[$lt];  # Location of first item for latitude lt.
//		$num = @{$lonsperlat{$quad}}[$lt];  # Number of items for latitude lt.
//		# print " * beg = $beg num = $num\n";
		int beg = mlatbegins.get(vquad)[vlt];
		int num = mlonsperlat.get(vquad)[vlt];
	
//		System.out.println(" * beg = " + beg + " num = " + num);
		

//		# Extract this tier of longitude and f-e numbers for latitude lt.
//		@mylons = splice(@{$lons{$quad}},$beg,$num);
//		@myfenums = splice(@{$fenums{$quad}},$beg,$num);
		
		int[] amylons = Arrays.copyOfRange(mlons.get(vquad), beg, beg+num);
		int[] amyfenums = Arrays.copyOfRange(mfenums.get(vquad), beg, beg+num);
		
//		#$mylons = join(" ",@mylons);
//		#$myfenums = join(" ",@myfenums);
//		#print "mylons\n$mylons\n";
//		#print "myfenums\n$myfenums\n";
		System.out.println(Arrays.toString(amylons));
		System.out.println(Arrays.toString(amyfenums));
		
//		$n = 0;
//		foreach $long (@mylons) {
//		   if ( $long > $ln ) { last; }
//		   $n++;
//		}
		int n = 0;
		for (int item :amylons) {
			if (item > vln)  {
				break;
			}
			n++;
		}
		
//		$feindex = $n - 1;
//		$fenum = $myfenums[$feindex];
//		$fename = $names[$fenum-1];
		
		int vfeindex = n - 1;
		int vfenum = amyfenums[vfeindex];
		String vfename = anames[vfenum-1];
		
//		# print " $long $n $feindex $fenum $fename\n";
//		print "$fename\n";
		
		System.out.printf("%d %d %d %s", n, vfeindex, vfenum, vfename);
		System.out.println();
		System.out.println(vfename);
	}

	

}
