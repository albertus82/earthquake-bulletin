package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class FERegionTest {

	private static FERegion instance;

	@BeforeAll
	static void init() throws IOException {
		instance = new FERegion();
	}

	@Test
	void testCoordinates() {
		final Coordinates c1 = new Coordinates(12, -34);
		log.info("{}", c1);
		Assertions.assertNotNull(c1);
		Assertions.assertNotEquals(c1, new Object());
		final Coordinates c2 = new Coordinates(-156, 78);
		log.info("{}", c2);
		Assertions.assertNotEquals(c1, c2);
		final Coordinates c3 = Coordinates.parse("34S", "12E");
		log.info("{}", c3);
		Assertions.assertEquals(c1, c3);
		Assertions.assertNotEquals(c2, c3);
	}

	@Test
	void testRegion() {
		final Region r1 = instance.getGeographicRegion(new Coordinates(12.5, 42.5));
		log.info("{}", r1);
		Assertions.assertNotNull(r1);
		Assertions.assertNotEquals(r1, new Object());
		final Region r2 = instance.getGeographicRegion(new Coordinates(-12.5, -42.5));
		log.info("{}", r2);
		Assertions.assertNotEquals(r1, r2);
		final Region r3 = instance.getGeographicRegion("12E", "42");
		log.info("{}", r3);
		Assertions.assertEquals(r1, r3);
		Assertions.assertNotEquals(r2, r3);
		Assertions.assertEquals(381, r1.getNumber());
		Assertions.assertEquals("CENTRAL ITALY", r1.getName());
		Assertions.assertEquals(0, r1.compareTo(r3));
		Assertions.assertTrue(r1.compareTo(r2) < 0, r1.getNumber() + " < " + r2.getNumber());
		Assertions.assertTrue(r2.compareTo(r1) > 0, r2.getNumber() + " > " + r1.getNumber());
		final Region r4 = new Region(333, "AAA");
		log.info("{}", r4);
		final Region r5 = new Region(333, "bbb");
		log.info("{}", r5);
		Assertions.assertEquals(r4, r5);
		Assertions.assertEquals(r4.hashCode(), r5.hashCode());
		final Region r6 = new Region(555, "CCC");
		log.info("{}", r6);
		final Region r7 = new Region(555, "CCC");
		log.info("{}", r7);
		Assertions.assertEquals(r6, r7);
		Assertions.assertEquals(r6.hashCode(), r7.hashCode());
		final Region r8 = new Region(111, "ddd");
		log.info("{}", r8);
		final Region r9 = new Region(222, "ddd");
		log.info("{}", r9);
		Assertions.assertNotEquals(r8, r9);
		final Region r10 = new Region(444, "EEE");
		log.info("{}", r10);
		final Region r11 = new Region(222, "fff");
		log.info("{}", r11);
		Assertions.assertNotEquals(r10, r11);
	}

	@Test
	void compareWithPerlOutput() throws IOException {
		try (final InputStream is = getClass().getResourceAsStream("perl.out.gz"); final GZIPInputStream gzis = new GZIPInputStream(is); final InputStreamReader isr = new InputStreamReader(gzis, StandardCharsets.US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
			for (int lon = -180; lon <= 180; lon++) {
				for (int lat = -90; lat <= 90; lat++) {
					final String name = instance.getGeographicRegion(Integer.toString(lon), Integer.toString(lat)).getName();
					Assertions.assertEquals(br.readLine(), name, "lon: " + lon + ", lat: " + lat);
				}
			}
		}
		try (final InputStream is = getClass().getResourceAsStream("perlext.out.gz"); final GZIPInputStream gzis = new GZIPInputStream(is); final InputStreamReader isr = new InputStreamReader(gzis, StandardCharsets.US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
			for (int lon = -540; lon <= 540; lon += 5) {
				for (int lat = -90; lat <= 90; lat += 5) {
					final String name = instance.getGeographicRegion(Integer.toString(lon), Integer.toString(lat)).getName();
					Assertions.assertEquals(br.readLine(), name, "lon: " + lon + ", lat: " + lat);
				}
			}
		}
	}

	@Test
	void testCli() {
		testGetName("0", "0", "OFF S. COAST OF NORTHWEST AFRICA");

		testGetName("180", "+90", "LOMONOSOV RIDGE");
		testGetName("-180", "90", "LOMONOSOV RIDGE");
		testGetName("+180E", "-90", "ANTARCTICA");
		testGetName("180W", "-90", "ANTARCTICA");

		testGetName("12", "42", "CENTRAL ITALY");
		testGetName("+12", "+42", "CENTRAL ITALY");
		testGetName("12E", "42N", "CENTRAL ITALY");
		testGetName("42N", "12E", "CENTRAL ITALY");
		testGetName("42S", "12E", "SOUTHWEST OF AFRICA");
		testGetName("12E", "42S", "SOUTHWEST OF AFRICA");
		testGetName("12", "-42", "SOUTHWEST OF AFRICA");
		testGetName("-42", "12", "NORTH ATLANTIC OCEAN");
		testGetName("42W", "12", "NORTH ATLANTIC OCEAN");
		testGetName("42W", "12N", "NORTH ATLANTIC OCEAN");
		testGetName("-12", "-42", "TRISTAN DA CUNHA REGION");
		testGetName("12W", "42S", "TRISTAN DA CUNHA REGION");
		testGetName("42S", "12W", "TRISTAN DA CUNHA REGION");
		testGetName("12", "42N", "CENTRAL ITALY");
		testGetName("12E", "42", "CENTRAL ITALY");
		testGetName("42N", "12", "CENTRAL ITALY");
		testGetName("181", "90", "LOMONOSOV RIDGE");
		testGetName("540", "-90", "ANTARCTICA");
	}

	@Test
	void testCliErrors() {
		try {
			testGetName("90", "91", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("0", "90.1", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("541", "90", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("540.01", "90", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("-90", "-91", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("-540.01", "90", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("-4873593.4834", "395953", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("42Sx", "12W", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("x", "y", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("12R", "42N", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
		try {
			testGetName("42", "12E", null);
			Assertions.assertTrue(false);
		}
		catch (final IllegalCoordinateException e) {
			Assertions.assertTrue(true);
		}
	}

	@Test
	void testGetRegionByNumber() {
		try {
			instance.getGeographicRegion(0);
			Assertions.assertTrue(false);
		}
		catch (final IndexOutOfBoundsException e) {
			Assertions.assertNotNull(e);
		}

		Assertions.assertEquals("CENTRAL ALASKA", instance.getGeographicRegion(1).getName());
		Assertions.assertEquals("PYRENEES", instance.getGeographicRegion(378).getName());
		Assertions.assertEquals("GALAPAGOS TRIPLE JUNCTION REGION", instance.getGeographicRegion(757).getName());

		try {
			instance.getGeographicRegion(758);
			Assertions.assertTrue(false);
		}
		catch (final IndexOutOfBoundsException e) {
			Assertions.assertNotNull(e);
		}
	}

	@Test
	void testRealCases() {
		final List<TestCase> testCases = new ArrayList<>(1000);
		testCases.add(new TestCase("11.03N", "124.90E", "Leyte, Philippines"));
		testCases.add(new TestCase("14.30N", "93.26W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("6.71S", "127.38E", "Banda Sea"));
		testCases.add(new TestCase("3.10S", "128.98E", "Seram, Indonesia"));
		testCases.add(new TestCase("20.03S", "70.23W", "Near Coast of Northern Chile"));
		testCases.add(new TestCase("5.41S", "146.90E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("72.10N", "1.59W", "Jan Mayen Island Region"));
		testCases.add(new TestCase("51.66N", "16.12E", "Poland"));
		testCases.add(new TestCase("29.45N", "81.15E", "Nepal"));
		testCases.add(new TestCase("41.30N", "142.44E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("1.04N", "124.88E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("6.03S", "150.85E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("9.29S", "113.51E", "South of Java, Indonesia"));
		testCases.add(new TestCase("15.56S", "171.59W", "Samoa Islands Region"));
		testCases.add(new TestCase("17.60S", "178.51W", "Fiji Islands Region"));
		testCases.add(new TestCase("40.79N", "13.86E", "Tyrrhenian Sea"));
		testCases.add(new TestCase("14.45N", "92.88W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("19.39S", "172.99W", "Tonga Islands Region"));
		testCases.add(new TestCase("27.62N", "86.19E", "Nepal"));
		testCases.add(new TestCase("33.17S", "70.08W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("32.63N", "140.06E", "Southeast of Honshu, Japan"));
		testCases.add(new TestCase("62.14N", "149.63W", "Central Alaska"));
		testCases.add(new TestCase("30.46N", "94.90E", "Xizang"));
		testCases.add(new TestCase("45.68N", "152.85E", "East of Kuril Islands"));
		testCases.add(new TestCase("1.28S", "120.52E", "Sulawesi, Indonesia"));
		testCases.add(new TestCase("30.43N", "94.90E", "Xizang"));
		testCases.add(new TestCase("35.39S", "179.63W", "East of North Island, N.Z."));
		testCases.add(new TestCase("14.09N", "91.77W", "Guatemala"));
		testCases.add(new TestCase("13.65S", "75.58W", "Central Peru"));
		testCases.add(new TestCase("25.67N", "128.48E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("12.55N", "86.74W", "Nicaragua"));
		testCases.add(new TestCase("20.32S", "68.98W", "Chile-Bolivia Border Region"));
		testCases.add(new TestCase("16.29S", "169.94E", "Vanuatu Islands"));
		testCases.add(new TestCase("25.50S", "71.02W", "Off Coast of Northern Chile"));
		testCases.add(new TestCase("40.29N", "143.63E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("4.19S", "103.72E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("21.02S", "178.75W", "Fiji Islands Region"));
		testCases.add(new TestCase("21.02S", "178.80W", "Fiji Islands Region"));
		testCases.add(new TestCase("18.11N", "79.78W", "North of Honduras"));
		testCases.add(new TestCase("56.11N", "164.05E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("15.44S", "173.01W", "Tonga Islands"));
		testCases.add(new TestCase("30.25N", "142.12E", "Southeast of Honshu, Japan"));
		testCases.add(new TestCase("40.36S", "176.04E", "North Island, New Zealand"));
		testCases.add(new TestCase("55.52S", "29.11W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("6.04N", "146.50E", "E. Caroline Islands, Micronesia"));
		testCases.add(new TestCase("81.41N", "2.84W", "North of Svalbard"));
		testCases.add(new TestCase("17.97S", "178.75W", "Fiji Islands Region"));
		testCases.add(new TestCase("53.07N", "159.73E", "Near East Coast of Kamchatka"));
		testCases.add(new TestCase("6.25S", "128.09E", "Banda Sea"));
		testCases.add(new TestCase("45.66S", "76.79W", "Off Coast of Southern Chile"));
		testCases.add(new TestCase("18.88N", "66.38W", "Puerto Rico Region"));
		testCases.add(new TestCase("36.55N", "71.36E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("6.19S", "130.41E", "Banda Sea"));
		testCases.add(new TestCase("1.24S", "14.73W", "North of Ascension Island"));
		testCases.add(new TestCase("24.35S", "179.70E", "South of Fiji Islands"));
		testCases.add(new TestCase("14.31S", "72.56W", "Central Peru"));
		testCases.add(new TestCase("36.90N", "27.73E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.92N", "27.64E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("32.39N", "46.24E", "Iran-Iraq Border Region"));
		testCases.add(new TestCase("55.91N", "166.15E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("17.27N", "100.13W", "Guerrero, Mexico"));
		testCases.add(new TestCase("37.54N", "37.67E", "Turkey"));
		testCases.add(new TestCase("34.67N", "23.96E", "Crete, Greece"));
		testCases.add(new TestCase("1.04S", "13.79W", "North of Ascension Island"));
		testCases.add(new TestCase("51.32N", "178.78E", "Rat Islands, Aleutian Islands"));
		testCases.add(new TestCase("15.86S", "71.66W", "Southern Peru"));
		testCases.add(new TestCase("5.94S", "12.52W", "Ascension Island Region"));
		testCases.add(new TestCase("6.32S", "127.95E", "Banda Sea"));
		testCases.add(new TestCase("2.84N", "128.52E", "Halmahera, Indonesia"));
		testCases.add(new TestCase("1.42N", "126.35E", "Northern Molucca Sea"));
		testCases.add(new TestCase("80.38N", "123.13E", "East of Severnaya Zemlya"));
		testCases.add(new TestCase("24.81S", "13.55W", "Southern Mid Atlantic Ridge"));
		testCases.add(new TestCase("29.29S", "176.09W", "Kermadec Islands Region"));
		testCases.add(new TestCase("36.50N", "24.64E", "Southern Greece"));
		testCases.add(new TestCase("24.24S", "67.11W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("28.78N", "127.84E", "Northwest of Ryukyu Islands"));
		testCases.add(new TestCase("22.05S", "67.20W", "Chile-Bolivia Border Region"));
		testCases.add(new TestCase("4.09N", "32.36W", "Central Mid Atlantic Ridge"));
		testCases.add(new TestCase("11.47N", "139.08E", "W. Caroline Islands, Micronesia"));
		testCases.add(new TestCase("6.91S", "129.74E", "Banda Sea"));
		testCases.add(new TestCase("2.28S", "99.92E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("13.13N", "88.00W", "El Salvador"));
		testCases.add(new TestCase("32.72N", "76.44E", "Kashmir-India Border Region"));
		testCases.add(new TestCase("17.86S", "178.63W", "Fiji Islands Region"));
		testCases.add(new TestCase("7.32S", "129.23E", "Banda Sea"));
		testCases.add(new TestCase("45.99N", "28.01W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("45.57N", "28.19W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("38.10N", "74.02E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("26.79N", "130.03E", "Southeast of Ryukyu Islands"));
		testCases.add(new TestCase("29.91S", "72.26W", "Off Coast of Central Chile"));
		testCases.add(new TestCase("3.02N", "128.10E", "North of Halmahera, Indonesia"));
		testCases.add(new TestCase("35.52N", "5.75E", "Northern Algeria"));
		testCases.add(new TestCase("15.00S", "173.23W", "Tonga Islands"));
		testCases.add(new TestCase("29.52S", "176.01W", "Kermadec Islands Region"));
		testCases.add(new TestCase("4.09S", "129.83E", "Banda Sea"));
		testCases.add(new TestCase("45.37N", "124.83E", "Northeastern China"));
		testCases.add(new TestCase("43.87S", "82.08W", "West Chile Rise"));
		testCases.add(new TestCase("43.87S", "82.08W", "West Chile Rise"));
		testCases.add(new TestCase("3.79S", "140.36E", "Irian Jaya, Indonesia"));
		testCases.add(new TestCase("19.25N", "65.25W", "Puerto Rico Region"));
		testCases.add(new TestCase("35.95N", "34.00W", "Azores Islands Region"));
		testCases.add(new TestCase("6.28S", "130.61E", "Banda Sea"));
		testCases.add(new TestCase("41.41N", "83.82E", "Southern Xinjiang, China"));
		testCases.add(new TestCase("35.80N", "140.26E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("18.97S", "168.50E", "Vanuatu Islands"));
		testCases.add(new TestCase("29.65N", "95.52E", "Eastern Xizang-India Border Reg."));
		testCases.add(new TestCase("11.01S", "75.22W", "Central Peru"));
		testCases.add(new TestCase("55.30S", "129.32W", "Pacific Antarctic Ridge"));
		testCases.add(new TestCase("39.68N", "143.30E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("37.02N", "27.77E", "Turkey"));
		testCases.add(new TestCase("48.57N", "27.82W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("48.68N", "27.94W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("48.79N", "27.97W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("48.77N", "26.79W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("48.93N", "27.97W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("10.71S", "74.52W", "Central Peru"));
		testCases.add(new TestCase("37.15N", "27.72E", "Turkey"));
		testCases.add(new TestCase("7.28N", "71.93W", "Venezuela"));
		testCases.add(new TestCase("37.14N", "27.74E", "Turkey"));
		testCases.add(new TestCase("18.99S", "169.49E", "Vanuatu Islands"));
		testCases.add(new TestCase("37.05N", "27.81E", "Turkey"));
		testCases.add(new TestCase("27.47N", "56.41E", "Southern Iran"));
		testCases.add(new TestCase("5.69S", "12.74W", "Ascension Island Region"));
		testCases.add(new TestCase("7.73S", "115.99E", "Bali Sea"));
		testCases.add(new TestCase("10.04S", "160.86E", "Solomon Islands"));
		testCases.add(new TestCase("3.62S", "101.71E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("22.05N", "121.22E", "Taiwan Region"));
		testCases.add(new TestCase("5.73S", "153.57E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("30.07N", "131.05E", "Kyushu, Japan"));
		testCases.add(new TestCase("4.28S", "152.98E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("13.96N", "120.81E", "Mindoro, Philippines"));
		testCases.add(new TestCase("30.36N", "94.89E", "Xizang"));
		testCases.add(new TestCase("20.05S", "70.84W", "Near Coast of Northern Chile"));
		testCases.add(new TestCase("55.58N", "151.61W", "South of Alaska"));
		testCases.add(new TestCase("29.30S", "176.13W", "Kermadec Islands Region"));
		testCases.add(new TestCase("30.24S", "72.05W", "Off Coast of Central Chile"));
		testCases.add(new TestCase("55.53S", "30.11W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("2.69S", "129.54E", "Seram, Indonesia"));
		testCases.add(new TestCase("6.32S", "130.99E", "Banda Sea"));
		testCases.add(new TestCase("8.87S", "75.46W", "Central Peru"));
		testCases.add(new TestCase("28.12S", "67.29W", "La Rioja Province, Argentina"));
		testCases.add(new TestCase("16.22S", "73.34W", "Near Coast of Peru"));
		testCases.add(new TestCase("29.64N", "128.99E", "Northwest of Ryukyu Islands"));
		testCases.add(new TestCase("6.71S", "131.27E", "Tanimbar Islands Reg., Indonesia"));
		testCases.add(new TestCase("52.60N", "169.14W", "Fox Islands, Aleutian Islands"));
		testCases.add(new TestCase("23.11S", "67.63W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("60.27N", "152.56W", "Southern Alaska"));
		testCases.add(new TestCase("14.02N", "120.70E", "Luzon, Philippines"));
		testCases.add(new TestCase("24.21S", "66.91W", "Salta Province, Argentina"));
		testCases.add(new TestCase("35.10N", "23.06E", "Crete, Greece"));
		testCases.add(new TestCase("39.29N", "15.73E", "Southern Italy"));
		testCases.add(new TestCase("23.21N", "121.53E", "Taiwan"));
		testCases.add(new TestCase("53.71N", "163.50W", "Unimak Island Region, Alaska"));
		testCases.add(new TestCase("42.17N", "125.54W", "Off Coast of Oregon"));
		testCases.add(new TestCase("1.19N", "124.13E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("38.84N", "70.63E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("27.99N", "54.25E", "Southern Iran"));
		testCases.add(new TestCase("14.28N", "92.18W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("24.83S", "13.58W", "Southern Mid Atlantic Ridge"));
		testCases.add(new TestCase("35.16N", "68.77E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("50.45N", "157.26E", "Kuril Islands"));
		testCases.add(new TestCase("41.42N", "29.28W", "Azores Islands Region"));
		testCases.add(new TestCase("8.13S", "103.89E", "Southwest of Sumatra, Indonesia"));
		testCases.add(new TestCase("35.41N", "69.26E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("41.62N", "29.45W", "Azores Islands Region"));
		testCases.add(new TestCase("54.52N", "134.41W", "Queen Charlotte Islands Region"));
		testCases.add(new TestCase("9.28S", "158.72E", "Solomon Islands"));
		testCases.add(new TestCase("9.50N", "124.14E", "Mindanao, Philippines"));
		testCases.add(new TestCase("35.67N", "140.26E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("37.04N", "27.76E", "Turkey"));
		testCases.add(new TestCase("7.59S", "122.00E", "Flores Sea"));
		testCases.add(new TestCase("45.03N", "20.87E", "NW Balkan Region"));
		testCases.add(new TestCase("33.39N", "104.11E", "Gansu, China"));
		testCases.add(new TestCase("45.19N", "14.54E", "NW Balkan Region"));
		testCases.add(new TestCase("19.79S", "178.10W", "Fiji Islands Region"));
		testCases.add(new TestCase("17.60S", "178.76W", "Fiji Islands Region"));
		testCases.add(new TestCase("52.62N", "169.15W", "Fox Islands, Aleutian Islands"));
		testCases.add(new TestCase("7.64S", "119.36E", "Flores Sea"));
		testCases.add(new TestCase("45.22N", "14.58E", "NW Balkan Region"));
		testCases.add(new TestCase("24.38S", "67.17W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("5.09S", "102.94E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("0.83N", "126.39E", "Northern Molucca Sea"));
		testCases.add(new TestCase("20.59S", "173.10W", "Tonga Islands"));
		testCases.add(new TestCase("52.46N", "169.21W", "Fox Islands, Aleutian Islands"));
		testCases.add(new TestCase("52.49N", "169.11W", "Fox Islands, Aleutian Islands"));
		testCases.add(new TestCase("44.33N", "82.82E", "Northern Xinjiang, China"));
		testCases.add(new TestCase("9.55N", "126.25E", "Mindanao, Philippines"));
		testCases.add(new TestCase("33.30N", "103.94E", "Gansu, China"));
		testCases.add(new TestCase("38.37N", "44.28E", "Turkey-Iran Border Region"));
		testCases.add(new TestCase("33.30N", "103.87E", "Gansu, China"));
		testCases.add(new TestCase("44.36N", "82.74E", "Northern Xinjiang, China"));
		testCases.add(new TestCase("44.31N", "82.91E", "Northern Xinjiang, China"));
		testCases.add(new TestCase("44.31N", "82.82E", "Northern Xinjiang, China"));
		testCases.add(new TestCase("3.25S", "152.39E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("45.19N", "14.36E", "NW Balkan Region"));
		testCases.add(new TestCase("52.71N", "172.53E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("6.49N", "72.09W", "Northern Colombia"));
		testCases.add(new TestCase("45.23N", "14.55E", "NW Balkan Region"));
		testCases.add(new TestCase("33.10N", "103.88E", "Gansu, China"));
		testCases.add(new TestCase("5.34S", "102.43E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("45.21N", "14.60E", "NW Balkan Region"));
		testCases.add(new TestCase("45.65N", "26.49E", "Romania"));
		testCases.add(new TestCase("36.90N", "27.66E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("26.24N", "126.70E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("42.44N", "15.13E", "Adriatic Sea"));
		testCases.add(new TestCase("36.94N", "27.65E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("37.01N", "27.66E", "Turkey"));
		testCases.add(new TestCase("53.09N", "35.17W", "Reykjanes Ridge"));
		testCases.add(new TestCase("14.94N", "145.87E", "Mariana Islands"));
		testCases.add(new TestCase("31.66S", "178.46W", "Kermadec Islands Region"));
		testCases.add(new TestCase("31.84N", "96.08E", "Xizang"));
		testCases.add(new TestCase("3.44S", "146.77E", "Bismarck Sea"));
		testCases.add(new TestCase("3.41S", "146.85E", "Bismarck Sea"));
		testCases.add(new TestCase("3.39S", "146.87E", "Bismarck Sea"));
		testCases.add(new TestCase("9.57N", "85.93W", "Off Coast of Costa Rica"));
		testCases.add(new TestCase("5.45S", "151.78E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("12.98N", "89.87W", "Off Coast of Central America"));
		testCases.add(new TestCase("37.01N", "27.64E", "Turkey"));
		testCases.add(new TestCase("36.97N", "27.63E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("33.34S", "57.13E", "Southwest Indian Ridge"));
		testCases.add(new TestCase("5.83S", "12.69W", "Ascension Island Region"));
		testCases.add(new TestCase("47.59S", "100.18E", "Southeast Indian Ridge"));
		testCases.add(new TestCase("18.79S", "174.71W", "Tonga Islands"));
		testCases.add(new TestCase("8.90N", "71.32W", "Venezuela"));
		testCases.add(new TestCase("19.57S", "177.73W", "Fiji Islands Region"));
		testCases.add(new TestCase("2.52N", "84.53W", "Off Coast of Central America"));
		testCases.add(new TestCase("19.23S", "128.01E", "Western Australia"));
		testCases.add(new TestCase("6.28N", "94.96E", "Nicobar Islands, India Region"));
		testCases.add(new TestCase("10.19S", "161.32E", "Solomon Islands"));
		testCases.add(new TestCase("0.63S", "130.01E", "Irian Jaya Region, Indonesia"));
		testCases.add(new TestCase("2.15N", "97.80E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("51.42N", "178.79E", "Rat Islands, Aleutian Islands"));
		testCases.add(new TestCase("7.85N", "125.18E", "Mindanao, Philippines"));
		testCases.add(new TestCase("19.22S", "67.28W", "Southern Bolivia"));
		testCases.add(new TestCase("38.94N", "26.65E", "Aegean Sea"));
		testCases.add(new TestCase("51.63N", "178.58W", "Andreanof Islands, Aleutian Islands"));
		testCases.add(new TestCase("6.26N", "125.46E", "Mindanao, Philippines"));
		testCases.add(new TestCase("5.70S", "12.63W", "Ascension Island Region"));
		testCases.add(new TestCase("36.54N", "71.04E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("9.87S", "154.29E", "D'Entrecasteaux Islands Region"));
		testCases.add(new TestCase("24.36N", "123.66E", "Southwestern Ryukyu Islands, Japan"));
		testCases.add(new TestCase("45.64S", "77.37W", "Off Coast of Southern Chile"));
		testCases.add(new TestCase("19.90S", "173.66W", "Tonga Islands"));
		testCases.add(new TestCase("10.45S", "78.03W", "Near Coast of Peru"));
		testCases.add(new TestCase("2.78S", "129.55E", "Seram, Indonesia"));
		testCases.add(new TestCase("8.31S", "108.95E", "Java, Indonesia"));
		testCases.add(new TestCase("17.88S", "172.57W", "Tonga Islands Region"));
		testCases.add(new TestCase("17.75S", "172.78W", "Tonga Islands Region"));
		testCases.add(new TestCase("36.06N", "139.94E", "Eastern Honshu, Japan"));
		testCases.add(new TestCase("20.74S", "178.83W", "Fiji Islands Region"));
		testCases.add(new TestCase("39.94N", "73.62E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("28.36S", "71.22W", "Near Coast of Central Chile"));
		testCases.add(new TestCase("32.98S", "179.14W", "South of Kermadec Islands"));
		testCases.add(new TestCase("5.62S", "12.61W", "Ascension Island Region"));
		testCases.add(new TestCase("24.22N", "93.59E", "Myanmar-India Border Region"));
		testCases.add(new TestCase("20.79N", "144.68E", "Mariana Islands"));
		testCases.add(new TestCase("6.43S", "147.41E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("42.45N", "143.27E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("4.68N", "125.59E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("64.58N", "17.84W", "Iceland"));
		testCases.add(new TestCase("33.29S", "70.27W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("45.69N", "26.48E", "Romania"));
		testCases.add(new TestCase("5.92S", "150.89E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("13.64N", "49.38W", "North Atlantic Ocean"));
		testCases.add(new TestCase("36.14N", "140.14E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("56.08S", "27.03W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("38.83N", "73.15E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("0.05N", "98.71E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("49.54S", "117.28E", "Western Indian Antarctic Ridge"));
		testCases.add(new TestCase("36.81N", "140.47E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("45.56N", "26.47E", "Romania"));
		testCases.add(new TestCase("6.08S", "154.38E", "Solomon Islands"));
		testCases.add(new TestCase("4.96S", "153.17E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("28.61S", "68.72W", "La Rioja Province, Argentina"));
		testCases.add(new TestCase("51.48N", "175.96W", "Andreanof Islands, Aleutian Islands"));
		testCases.add(new TestCase("53.01S", "4.92W", "Southern Mid Atlantic Ridge"));
		testCases.add(new TestCase("55.12S", "30.52W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("34.48N", "24.02E", "Crete, Greece"));
		testCases.add(new TestCase("4.87S", "129.49E", "Banda Sea"));
		testCases.add(new TestCase("19.70S", "175.71W", "Tonga Islands"));
		testCases.add(new TestCase("80.30N", "0.10W", "North of Svalbard"));
		testCases.add(new TestCase("2.67S", "102.25E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("38.34N", "141.68E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("54.10N", "165.41W", "Fox Islands, Aleutian Islands"));
		testCases.add(new TestCase("21.29S", "178.74W", "Fiji Islands Region"));
		testCases.add(new TestCase("14.43S", "167.22E", "Vanuatu Islands"));
		testCases.add(new TestCase("48.15N", "154.23E", "Kuril Islands"));
		testCases.add(new TestCase("28.79S", "177.00W", "Kermadec Islands Region"));
		testCases.add(new TestCase("28.83S", "176.95W", "Kermadec Islands Region"));
		testCases.add(new TestCase("46.15N", "150.99E", "Kuril Islands"));
		testCases.add(new TestCase("37.88N", "68.25E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("36.99N", "27.72E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("4.55S", "125.48E", "Banda Sea"));
		testCases.add(new TestCase("54.54S", "27.79W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("31.81N", "50.55E", "Northern and Central Iran"));
		testCases.add(new TestCase("36.22N", "143.75E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("0.91S", "150.37E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("4.82S", "68.76E", "Chagos Archipelago Region"));
		testCases.add(new TestCase("0.05N", "30.07E", "Uganda"));
		testCases.add(new TestCase("37.03N", "27.65E", "Turkey"));
		testCases.add(new TestCase("4.73N", "75.14W", "Colombia"));
		testCases.add(new TestCase("20.94S", "178.72W", "Fiji Islands Region"));
		testCases.add(new TestCase("21.14S", "176.15W", "Fiji Islands Region"));
		testCases.add(new TestCase("5.04S", "151.56E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("41.28N", "125.09W", "Off Coast of N. California"));
		testCases.add(new TestCase("14.97N", "93.76W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("22.55S", "173.00E", "Southeast of Loyalty Islands"));
		testCases.add(new TestCase("40.90N", "124.90W", "Near Coast of N. California"));
		testCases.add(new TestCase("36.35N", "142.22E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("36.30N", "141.94E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("16.09S", "167.83E", "Vanuatu Islands"));
		testCases.add(new TestCase("10.74N", "86.30W", "Off Coast of Costa Rica"));
		testCases.add(new TestCase("13.41N", "50.69E", "Eastern Gulf of Aden"));
		testCases.add(new TestCase("13.33N", "50.85E", "Eastern Gulf of Aden"));
		testCases.add(new TestCase("54.28N", "169.30E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("33.81N", "48.33E", "Western Iran"));
		testCases.add(new TestCase("10.21S", "161.57E", "Solomon Islands"));
		testCases.add(new TestCase("5.70S", "146.40E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("29.32N", "143.02E", "Southeast of Honshu, Japan"));
		testCases.add(new TestCase("24.20S", "67.25W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("4.42S", "101.89E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("13.41N", "49.35W", "North Atlantic Ocean"));
		testCases.add(new TestCase("3.93S", "129.07E", "Seram, Indonesia"));
		testCases.add(new TestCase("40.23N", "25.20E", "Aegean Sea"));
		testCases.add(new TestCase("3.59S", "126.14E", "Buru, Indonesia"));
		testCases.add(new TestCase("39.00N", "141.76E", "Eastern Honshu, Japan"));
		testCases.add(new TestCase("21.57N", "121.56E", "Taiwan Region"));
		testCases.add(new TestCase("38.21N", "73.98E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("5.14S", "145.48E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("63.78N", "18.96W", "Iceland"));
		testCases.add(new TestCase("7.51S", "75.57W", "Northern Peru"));
		testCases.add(new TestCase("7.89S", "117.62E", "Bali Sea"));
		testCases.add(new TestCase("23.49S", "179.84W", "South of Fiji Islands"));
		testCases.add(new TestCase("6.78N", "72.77W", "Northern Colombia"));
		testCases.add(new TestCase("26.88N", "130.20E", "Southeast of Ryukyu Islands"));
		testCases.add(new TestCase("29.59S", "69.27W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("26.95N", "130.11E", "Southeast of Ryukyu Islands"));
		testCases.add(new TestCase("24.05S", "69.40W", "Northern Chile"));
		testCases.add(new TestCase("1.00N", "122.69E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("49.70N", "0.20W", "France"));
		testCases.add(new TestCase("13.26N", "87.74W", "Honduras"));
		testCases.add(new TestCase("5.67S", "101.40E", "Southwest of Sumatra, Indonesia"));
		testCases.add(new TestCase("5.72S", "101.34E", "Southwest of Sumatra, Indonesia"));
		testCases.add(new TestCase("56.82S", "147.62E", "West of Macquarie Island"));
		testCases.add(new TestCase("31.74S", "67.08W", "San Juan Province, Argentina"));
		testCases.add(new TestCase("10.34N", "121.68E", "Panay, Philippines"));
		testCases.add(new TestCase("22.30S", "178.21W", "South of Fiji Islands"));
		testCases.add(new TestCase("54.00N", "159.37E", "Near East Coast of Kamchatka"));
		testCases.add(new TestCase("18.71N", "145.74E", "Mariana Islands"));
		testCases.add(new TestCase("37.29N", "141.82E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("39.49N", "141.99E", "Eastern Honshu, Japan"));
		testCases.add(new TestCase("34.62S", "179.74E", "South of Kermadec Islands"));
		testCases.add(new TestCase("22.59S", "66.19W", "Jujuy Province, Argentina"));
		testCases.add(new TestCase("23.36S", "175.19W", "Tonga Islands Region"));
		testCases.add(new TestCase("37.03N", "27.58E", "Turkey"));
		testCases.add(new TestCase("41.61N", "88.31E", "Southern Xinjiang, China"));
		testCases.add(new TestCase("54.19N", "169.33E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("47.34S", "11.81W", "Southern Mid Atlantic Ridge"));
		testCases.add(new TestCase("40.16N", "143.46E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("61.52S", "160.98E", "Balleny Islands Region"));
		testCases.add(new TestCase("35.71S", "178.56E", "Off E. Coast of N. Island, N.Z."));
		testCases.add(new TestCase("6.69N", "73.09W", "Northern Colombia"));
		testCases.add(new TestCase("54.45N", "168.94E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("38.78N", "20.44E", "Greece"));
		testCases.add(new TestCase("6.53S", "11.39W", "Ascension Island Region"));
		testCases.add(new TestCase("21.95S", "68.51W", "Chile-Bolivia Border Region"));
		testCases.add(new TestCase("20.11S", "173.36W", "Tonga Islands"));
		testCases.add(new TestCase("40.07N", "139.59E", "Near West Coast of Honshu, Japan"));
		testCases.add(new TestCase("3.95N", "122.60E", "Celebes Sea"));
		testCases.add(new TestCase("41.17N", "20.95E", "Albania"));
		testCases.add(new TestCase("5.94S", "154.72E", "Solomon Islands"));
		testCases.add(new TestCase("20.77S", "67.36W", "Southern Bolivia"));
		testCases.add(new TestCase("39.29N", "71.26E", "Tajikistan"));
		testCases.add(new TestCase("36.99N", "27.47E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("30.25N", "57.64E", "Northern and Central Iran"));
		testCases.add(new TestCase("40.11N", "143.30E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("49.81N", "156.21E", "Kuril Islands"));
		testCases.add(new TestCase("5.44N", "94.23E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("41.03N", "75.30E", "Kyrgyzstan"));
		testCases.add(new TestCase("16.12N", "95.83W", "Oaxaca, Mexico"));
		testCases.add(new TestCase("17.68S", "174.81W", "Tonga Islands"));
		testCases.add(new TestCase("0.29N", "120.19E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("53.72N", "170.75E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("45.38N", "124.36E", "Northeastern China"));
		testCases.add(new TestCase("40.05N", "27.11E", "Turkey"));
		testCases.add(new TestCase("24.30S", "67.12W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("36.49N", "26.76E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.92N", "27.41E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("32.27N", "140.45E", "Southeast of Honshu, Japan"));
		testCases.add(new TestCase("36.71N", "70.99E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("21.17S", "69.17W", "Northern Chile"));
		testCases.add(new TestCase("42.68N", "13.36E", "Central Italy"));
		testCases.add(new TestCase("40.11N", "143.30E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("36.99N", "27.65E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("55.83S", "28.46W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("36.92N", "27.48E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("45.68N", "10.71E", "Northern Italy"));
		testCases.add(new TestCase("15.71S", "74.23W", "Near Coast of Peru"));
		testCases.add(new TestCase("41.69N", "142.20E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("34.78N", "25.49E", "Crete, Greece"));
		testCases.add(new TestCase("36.92N", "27.71E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("16.49S", "173.55W", "Tonga Islands"));
		testCases.add(new TestCase("17.88S", "169.03E", "Vanuatu Islands"));
		testCases.add(new TestCase("36.32N", "70.94E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("31.42N", "48.91E", "Western Iran"));
		testCases.add(new TestCase("4.61N", "94.26E", "Off West Coast of Northern Sumatra"));
		testCases.add(new TestCase("35.71N", "140.28E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("37.01N", "27.40E", "Turkey"));
		testCases.add(new TestCase("36.93N", "27.73E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("53.48N", "161.43E", "Off East Coast of Kamchatka"));
		testCases.add(new TestCase("36.93N", "27.70E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.92N", "27.61E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.85N", "27.39E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.97N", "27.43E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.94N", "27.68E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.95N", "27.63E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("37.04N", "27.45E", "Turkey"));
		testCases.add(new TestCase("45.22N", "142.05E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("36.99N", "27.40E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("17.92N", "147.84E", "Mariana Islands Region"));
		testCases.add(new TestCase("36.93N", "27.37E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("54.97N", "167.64E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("36.97N", "27.44E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("36.96N", "27.51E", "Dodecanese Islands, Greece"));
		testCases.add(new TestCase("52.40N", "152.65E", "Northwest of Kuril Islands"));
		testCases.add(new TestCase("48.76N", "10.13E", "Germany"));
		testCases.add(new TestCase("21.68N", "144.35E", "Mariana Islands Region"));
		testCases.add(new TestCase("36.46N", "71.15E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("12.85N", "88.21W", "Off Coast of Central America"));
		testCases.add(new TestCase("4.71N", "126.88E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("38.42N", "21.97E", "Greece"));
		testCases.add(new TestCase("40.40N", "48.49E", "Eastern Caucasus"));
		testCases.add(new TestCase("37.15N", "71.77E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("53.28N", "171.54E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("8.99S", "110.48E", "Java, Indonesia"));
		testCases.add(new TestCase("0.32N", "127.26E", "Halmahera, Indonesia"));
		testCases.add(new TestCase("37.33N", "141.54E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("23.87S", "69.13W", "Northern Chile"));
		testCases.add(new TestCase("17.67S", "172.80W", "Tonga Islands Region"));
		testCases.add(new TestCase("1.79N", "124.87E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("36.30N", "71.10E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("12.29N", "58.67W", "North Atlantic Ocean"));
		testCases.add(new TestCase("17.51S", "66.60E", "Mauritius/Reunion Region"));
		testCases.add(new TestCase("14.42N", "90.30W", "Guatemala"));
		testCases.add(new TestCase("41.80N", "143.83E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("34.36N", "70.29E", "Southeastern Afghanistan"));
		testCases.add(new TestCase("26.35N", "126.58E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("13.40S", "167.11E", "Vanuatu Islands"));
		testCases.add(new TestCase("12.89N", "57.96E", "Owen Fracture Zone Region"));
		testCases.add(new TestCase("53.23N", "171.94E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("55.56N", "166.85E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("0.84N", "123.27E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("3.86S", "129.08E", "Seram, Indonesia"));
		testCases.add(new TestCase("53.18N", "171.56E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("34.34N", "137.58E", "Near S. Coast of Honshu, Japan"));
		testCases.add(new TestCase("55.61N", "166.48E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("55.15N", "167.11E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("54.56N", "168.76E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("53.47N", "171.02E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("3.25S", "140.15E", "Irian Jaya, Indonesia"));
		testCases.add(new TestCase("54.63N", "168.86E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("16.38S", "73.64W", "Near Coast of Peru"));
		testCases.add(new TestCase("55.46N", "166.51E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("53.85N", "170.10E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("54.19N", "168.95E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("54.39N", "169.06E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("53.13N", "171.05E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("54.61N", "168.88E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("5.49S", "103.21E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("5.61S", "129.19E", "Banda Sea"));
		testCases.add(new TestCase("6.71S", "127.41E", "Banda Sea"));
		testCases.add(new TestCase("18.06S", "178.41W", "Fiji Islands Region"));
		testCases.add(new TestCase("54.57N", "168.69E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("54.63N", "168.63E", "Komandorskiye Ostrova Region"));
		testCases.add(new TestCase("5.51S", "149.74E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("9.64S", "111.83E", "South of Java, Indonesia"));
		testCases.add(new TestCase("47.07N", "112.47W", "Montana"));
		testCases.add(new TestCase("7.73S", "127.60E", "Banda Sea"));
		testCases.add(new TestCase("23.79S", "179.56W", "South of Fiji Islands"));
		testCases.add(new TestCase("25.64N", "109.97W", "Gulf of California"));
		testCases.add(new TestCase("2.67N", "128.17E", "Halmahera, Indonesia"));
		testCases.add(new TestCase("19.17N", "66.85W", "Puerto Rico Region"));
		testCases.add(new TestCase("20.28S", "173.54W", "Tonga Islands"));
		testCases.add(new TestCase("32.30N", "105.48E", "Sichuan, China"));
		testCases.add(new TestCase("6.25S", "122.69W", "South Pacific Ocean"));
		testCases.add(new TestCase("20.41S", "173.49W", "Tonga Islands"));
		testCases.add(new TestCase("2.92S", "101.87E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("25.86S", "179.64E", "South of Fiji Islands"));
		testCases.add(new TestCase("77.03N", "18.79E", "Svalbard Region"));
		testCases.add(new TestCase("3.92S", "126.73E", "Buru, Indonesia"));
		testCases.add(new TestCase("47.43N", "11.52E", "Austria"));
		testCases.add(new TestCase("58.78S", "148.83E", "West of Macquarie Island"));
		testCases.add(new TestCase("34.80N", "25.43E", "Crete, Greece"));
		testCases.add(new TestCase("30.70N", "82.75E", "Xizang"));
		testCases.add(new TestCase("28.30N", "81.13E", "Nepal-India Border Region"));
		testCases.add(new TestCase("24.78S", "179.69W", "South of Fiji Islands"));
		testCases.add(new TestCase("0.48N", "122.06E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("36.35N", "70.92E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("45.46N", "136.86E", "Primor'ye, Russia"));
		testCases.add(new TestCase("20.12N", "146.62E", "Mariana Islands Region"));
		testCases.add(new TestCase("2.02N", "121.75E", "Celebes Sea"));
		testCases.add(new TestCase("38.38N", "22.08E", "Greece"));
		testCases.add(new TestCase("38.41N", "22.06E", "Greece"));
		testCases.add(new TestCase("10.63S", "161.75E", "Solomon Islands"));
		testCases.add(new TestCase("35.99N", "96.57W", "Oklahoma"));
		testCases.add(new TestCase("33.74S", "72.14W", "Off Coast of Central Chile"));
		testCases.add(new TestCase("33.74S", "72.48W", "Off Coast of Central Chile"));
		testCases.add(new TestCase("38.39N", "22.06E", "Greece"));
		testCases.add(new TestCase("1.37N", "99.15E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("5.44S", "147.17E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("48.62N", "154.89E", "Kuril Islands"));
		testCases.add(new TestCase("21.38S", "177.95W", "Fiji Islands Region"));
		testCases.add(new TestCase("1.43N", "99.25E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("56.14N", "160.65E", "Kamchatka Peninsula, Russia"));
		testCases.add(new TestCase("27.15S", "176.49W", "Kermadec Islands Region"));
		testCases.add(new TestCase("23.36S", "66.90W", "Jujuy Province, Argentina"));
		testCases.add(new TestCase("63.14N", "150.88W", "Central Alaska"));
		testCases.add(new TestCase("12.09N", "125.10E", "Samar, Philippines"));
		testCases.add(new TestCase("21.35S", "68.52W", "Chile-Bolivia Border Region"));
		testCases.add(new TestCase("11.62S", "166.75E", "Santa Cruz Islands"));
		testCases.add(new TestCase("28.86S", "68.98W", "La Rioja Province, Argentina"));
		testCases.add(new TestCase("8.30S", "119.34E", "Flores Region, Indonesia"));
		testCases.add(new TestCase("8.13S", "107.21E", "Java, Indonesia"));
		testCases.add(new TestCase("4.74N", "125.59E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("23.41N", "94.38E", "Myanmar-India Border Region"));
		testCases.add(new TestCase("4.76S", "153.14E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("0.92N", "123.90E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("41.09N", "20.88E", "Albania"));
		testCases.add(new TestCase("41.43S", "88.05W", "West Chile Rise"));
		testCases.add(new TestCase("40.80N", "131.82E", "Sea of Japan"));
		testCases.add(new TestCase("26.04N", "128.43E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("5.80N", "125.61E", "Mindanao, Philippines"));
		testCases.add(new TestCase("8.91N", "127.02E", "Philippine Islands Region"));
		testCases.add(new TestCase("24.23S", "67.36W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("37.70N", "101.66E", "Qinghai, China"));
		testCases.add(new TestCase("13.49N", "89.63W", "El Salvador"));
		testCases.add(new TestCase("35.36S", "72.98W", "Near Coast of Central Chile"));
		testCases.add(new TestCase("35.35S", "73.31W", "Off Coast of Central Chile"));
		testCases.add(new TestCase("50.29N", "12.44E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.28N", "12.44E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("35.32N", "141.22E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("60.17S", "48.52W", "Scotia Sea"));
		testCases.add(new TestCase("28.87S", "72.46W", "Off Coast of Central Chile"));
		testCases.add(new TestCase("50.28N", "12.46E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.29N", "12.44E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.29N", "12.42E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.28N", "12.42E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("9.22S", "113.06E", "South of Java, Indonesia"));
		testCases.add(new TestCase("50.30N", "12.41E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("46.20N", "143.03E", "Sakhalin, Russia"));
		testCases.add(new TestCase("50.30N", "12.35E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.28N", "12.45E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.28N", "12.42E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("10.08S", "118.26E", "South of Sumbawa, Indonesia"));
		testCases.add(new TestCase("0.78N", "79.64W", "Near Coast of Ecuador"));
		testCases.add(new TestCase("50.28N", "12.44E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.31N", "12.42E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.26N", "12.45E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("50.29N", "12.46E", "Vogtland (German-Czech Border Region)"));
		testCases.add(new TestCase("49.27S", "164.25E", "Auckland Islands, N.Z. Region"));
		testCases.add(new TestCase("35.73N", "1.93W", "Northern Algeria"));
		testCases.add(new TestCase("31.44N", "130.59E", "Kyushu, Japan"));
		testCases.add(new TestCase("2.72N", "98.85E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("7.34S", "13.61W", "Ascension Island Region"));
		testCases.add(new TestCase("32.25N", "48.06E", "Western Iran"));
		testCases.add(new TestCase("8.40S", "107.81E", "Java, Indonesia"));
		testCases.add(new TestCase("5.62S", "110.23E", "Java Sea"));
		testCases.add(new TestCase("36.18S", "100.71W", "Southeast of Easter Island"));
		testCases.add(new TestCase("23.52S", "179.82W", "South of Fiji Islands"));
		testCases.add(new TestCase("59.86N", "153.31W", "Southern Alaska"));
		testCases.add(new TestCase("5.13S", "151.29E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("4.19S", "129.82E", "Banda Sea"));
		testCases.add(new TestCase("23.36S", "179.44E", "South of Fiji Islands"));
		testCases.add(new TestCase("10.90N", "124.82E", "Leyte, Philippines"));
		testCases.add(new TestCase("5.49N", "82.34W", "South of Panama"));
		testCases.add(new TestCase("28.89N", "139.46E", "Bonin Islands, Japan Region"));
		testCases.add(new TestCase("2.73S", "100.78E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("34.41N", "82.61E", "Xizang"));
		testCases.add(new TestCase("23.83S", "179.54W", "South of Fiji Islands"));
		testCases.add(new TestCase("4.21S", "101.11E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("27.66N", "64.60E", "Southwestern Pakistan"));
		testCases.add(new TestCase("11.51S", "13.47W", "Ascension Island Region"));
		testCases.add(new TestCase("56.13S", "27.54W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("43.22N", "17.38E", "NW Balkan Region"));
		testCases.add(new TestCase("6.77S", "128.46E", "Banda Sea"));
		testCases.add(new TestCase("3.71N", "122.17E", "Celebes Sea"));
		testCases.add(new TestCase("11.62S", "77.18W", "Near Coast of Peru"));
		testCases.add(new TestCase("6.88S", "125.80E", "Banda Sea"));
		testCases.add(new TestCase("7.39S", "105.93E", "Java, Indonesia"));
		testCases.add(new TestCase("41.02N", "20.88E", "Albania"));
		testCases.add(new TestCase("2.55N", "66.31E", "Carlsberg Ridge"));
		testCases.add(new TestCase("41.12N", "20.86E", "Albania"));
		testCases.add(new TestCase("47.31N", "16.09E", "Austria"));
		testCases.add(new TestCase("10.78N", "86.39W", "Off Coast of Costa Rica"));
		testCases.add(new TestCase("43.86N", "147.91E", "Kuril Islands"));
		testCases.add(new TestCase("5.98S", "149.83E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("3.42S", "135.86E", "Irian Jaya Region, Indonesia"));
		testCases.add(new TestCase("10.67N", "82.98W", "North of Panama"));
		testCases.add(new TestCase("20.24S", "173.55W", "Tonga Islands"));
		testCases.add(new TestCase("37.44N", "141.79E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("35.82N", "1.92W", "Northern Algeria"));
		testCases.add(new TestCase("31.72N", "137.97E", "Southeast of Honshu, Japan"));
		testCases.add(new TestCase("34.60N", "73.51E", "Pakistan"));
		testCases.add(new TestCase("24.23S", "67.10W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("22.50S", "173.00E", "Southeast of Loyalty Islands"));
		testCases.add(new TestCase("43.03N", "13.14E", "Central Italy"));
		testCases.add(new TestCase("21.41S", "66.91W", "Southern Bolivia"));
		testCases.add(new TestCase("9.04S", "74.26W", "Central Peru"));
		testCases.add(new TestCase("46.59N", "7.00E", "Switzerland"));
		testCases.add(new TestCase("36.56N", "71.40E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("41.11N", "20.90E", "Albania"));
		testCases.add(new TestCase("41.15N", "20.95E", "Albania"));
		testCases.add(new TestCase("3.11N", "127.02E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("24.65S", "178.44W", "South of Fiji Islands"));
		testCases.add(new TestCase("17.21N", "120.95E", "Luzon, Philippines"));
		testCases.add(new TestCase("37.84N", "141.15E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("13.12N", "89.94W", "El Salvador"));
		testCases.add(new TestCase("61.32S", "154.30E", "Balleny Islands Region"));
		testCases.add(new TestCase("60.51N", "151.93W", "Kenai Peninsula, Alaska"));
		testCases.add(new TestCase("36.18N", "70.72E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("41.15N", "20.95E", "Albania"));
		testCases.add(new TestCase("6.74S", "147.91E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("33.59S", "71.99W", "Near Coast of Central Chile"));
		testCases.add(new TestCase("6.76S", "130.48E", "Banda Sea"));
		testCases.add(new TestCase("58.42N", "1.53E", "North Sea"));
		testCases.add(new TestCase("41.16N", "20.92E", "Albania"));
		testCases.add(new TestCase("53.62N", "160.29E", "Near East Coast of Kamchatka"));
		testCases.add(new TestCase("1.20S", "120.32E", "Sulawesi, Indonesia"));
		testCases.add(new TestCase("46.07N", "15.04E", "NW Balkan Region"));
		testCases.add(new TestCase("24.25S", "179.98W", "South of Fiji Islands"));
		testCases.add(new TestCase("24.23S", "179.94W", "South of Fiji Islands"));
		testCases.add(new TestCase("5.30S", "122.89E", "Sulawesi, Indonesia"));
		testCases.add(new TestCase("12.41S", "166.63E", "Santa Cruz Islands"));
		testCases.add(new TestCase("41.13N", "20.90E", "Albania"));
		testCases.add(new TestCase("11.10N", "124.83E", "Leyte, Philippines"));
		testCases.add(new TestCase("47.06N", "112.36W", "Montana"));
		testCases.add(new TestCase("38.31N", "143.53E", "Off East Coast of Honshu, Japan"));
		testCases.add(new TestCase("46.99N", "112.37W", "Montana"));
		testCases.add(new TestCase("20.86S", "173.87W", "Tonga Islands"));
		testCases.add(new TestCase("8.15S", "128.69E", "Timor Sea"));
		testCases.add(new TestCase("13.62N", "90.95W", "Near Coast of Guatemala"));
		testCases.add(new TestCase("0.52N", "126.15E", "Northern Molucca Sea"));
		testCases.add(new TestCase("21.62S", "170.57E", "Southeast of Loyalty Islands"));
		testCases.add(new TestCase("83.56N", "22.38E", "North of Svalbard"));
		testCases.add(new TestCase("35.55N", "69.87E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("41.03N", "20.90E", "Albania"));
		testCases.add(new TestCase("3.90S", "129.85E", "Seram, Indonesia"));
		testCases.add(new TestCase("33.37N", "2.93W", "Morocco"));
		testCases.add(new TestCase("6.62S", "128.64E", "Banda Sea"));
		testCases.add(new TestCase("39.39N", "53.50E", "Turkmenistan"));
		testCases.add(new TestCase("3.82S", "129.97E", "Seram, Indonesia"));
		testCases.add(new TestCase("41.12N", "20.91E", "Albania"));
		testCases.add(new TestCase("3.96S", "129.98E", "Seram, Indonesia"));
		testCases.add(new TestCase("3.90S", "129.93E", "Seram, Indonesia"));
		testCases.add(new TestCase("3.84S", "129.96E", "Seram, Indonesia"));
		testCases.add(new TestCase("35.56S", "73.19W", "Off Coast of Central Chile"));
		testCases.add(new TestCase("8.67S", "118.38E", "Sumbawa Region, Indonesia"));
		testCases.add(new TestCase("22.58S", "25.14E", "Botswana"));
		testCases.add(new TestCase("8.49S", "122.46E", "Flores Region, Indonesia"));
		testCases.add(new TestCase("5.98S", "101.92E", "Southwest of Sumatra, Indonesia"));
		testCases.add(new TestCase("41.14N", "20.94E", "Albania"));
		testCases.add(new TestCase("14.73S", "167.31E", "Vanuatu Islands"));
		testCases.add(new TestCase("49.70S", "117.37E", "Western Indian Antarctic Ridge"));
		testCases.add(new TestCase("6.06N", "126.02E", "Mindanao, Philippines"));
		testCases.add(new TestCase("39.59N", "73.15E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("23.98S", "66.92W", "Jujuy Province, Argentina"));
		testCases.add(new TestCase("5.12S", "123.20E", "Banda Sea"));
		testCases.add(new TestCase("44.40N", "101.49E", "Mongolia"));
		testCases.add(new TestCase("19.52S", "69.90W", "Northern Chile"));
		testCases.add(new TestCase("34.78S", "108.59W", "Southern East Pacific Rise"));
		testCases.add(new TestCase("11.24S", "162.87E", "Solomon Islands"));
		testCases.add(new TestCase("3.90S", "129.85E", "Seram, Indonesia"));
		testCases.add(new TestCase("3.88S", "129.88E", "Seram, Indonesia"));
		testCases.add(new TestCase("3.86S", "129.95E", "Seram, Indonesia"));
		testCases.add(new TestCase("41.18N", "20.96E", "Macedonia"));
		testCases.add(new TestCase("27.14N", "127.32E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("41.12N", "20.90E", "Macedonia"));
		testCases.add(new TestCase("34.93N", "26.74E", "Crete, Greece"));
		testCases.add(new TestCase("41.09N", "20.82E", "Macedonia"));
		testCases.add(new TestCase("36.52N", "70.14E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("37.39N", "141.29E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("41.17N", "20.91E", "Macedonia"));
		testCases.add(new TestCase("17.87S", "178.54W", "Fiji Islands Region"));
		testCases.add(new TestCase("3.61N", "73.92W", "Colombia"));
		testCases.add(new TestCase("21.94S", "68.50W", "Chile-Bolivia Border Region"));
		testCases.add(new TestCase("16.15N", "92.66W", "Chiapas, Mexico"));
		testCases.add(new TestCase("27.55S", "176.17W", "Kermadec Islands Region"));
		testCases.add(new TestCase("27.46S", "176.36W", "Kermadec Islands Region"));
		testCases.add(new TestCase("41.11N", "20.89E", "Macedonia"));
		testCases.add(new TestCase("39.67N", "53.58E", "Turkmenistan"));
		testCases.add(new TestCase("2.49N", "128.09E", "Halmahera, Indonesia"));
		testCases.add(new TestCase("24.16N", "94.56E", "Myanmar-India Border Region"));
		testCases.add(new TestCase("55.30N", "161.86E", "Near East Coast of Kamchatka"));
		testCases.add(new TestCase("27.38N", "86.45E", "Nepal"));
		testCases.add(new TestCase("42.69N", "13.36E", "Central Italy"));
		testCases.add(new TestCase("38.64N", "142.01E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("24.19S", "67.15W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("3.73S", "128.65E", "Seram, Indonesia"));
		testCases.add(new TestCase("32.91N", "131.12E", "Kyushu, Japan"));
		testCases.add(new TestCase("42.83N", "141.96E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("46.60N", "7.03E", "Switzerland"));
		testCases.add(new TestCase("23.63S", "66.53W", "Jujuy Province, Argentina"));
		testCases.add(new TestCase("23.69N", "94.57E", "Myanmar-India Border Region"));
		testCases.add(new TestCase("36.62N", "70.72E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("2.39N", "127.16E", "Northern Molucca Sea"));
		testCases.add(new TestCase("25.62N", "110.02W", "Gulf of California"));
		testCases.add(new TestCase("0.28S", "80.32W", "Near Coast of Ecuador"));
		testCases.add(new TestCase("7.31S", "128.00E", "Banda Sea"));
		testCases.add(new TestCase("25.53N", "125.33E", "Southwestern Ryukyu Islands, Japan"));
		testCases.add(new TestCase("58.87N", "1.85E", "North Sea"));
		testCases.add(new TestCase("13.59N", "90.97W", "Near Coast of Guatemala"));
		testCases.add(new TestCase("54.84N", "164.02W", "Unimak Island Region, Alaska"));
		testCases.add(new TestCase("12.46S", "76.78W", "Near Coast of Peru"));
		testCases.add(new TestCase("12.02N", "140.51E", "W. Caroline Islands, Micronesia"));
		testCases.add(new TestCase("0.41N", "99.85E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("33.82N", "38.60W", "Northern Mid Atlantic Ridge"));
		testCases.add(new TestCase("12.31N", "141.82E", "South of Mariana Islands"));
		testCases.add(new TestCase("42.66N", "13.25E", "Central Italy"));
		testCases.add(new TestCase("39.59N", "73.31E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("42.66N", "13.23E", "Central Italy"));
		testCases.add(new TestCase("37.31S", "72.39W", "Central Chile"));
		testCases.add(new TestCase("4.61S", "153.01E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("34.77N", "23.33E", "Crete, Greece"));
		testCases.add(new TestCase("5.77N", "125.92E", "Mindanao, Philippines"));
		testCases.add(new TestCase("60.18N", "153.88W", "Southern Alaska"));
		testCases.add(new TestCase("34.72S", "71.81W", "Near Coast of Central Chile"));
		testCases.add(new TestCase("30.95S", "179.85E", "Kermadec Islands Region"));
		testCases.add(new TestCase("28.42N", "128.77E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("41.17N", "20.91E", "Macedonia"));
		testCases.add(new TestCase("14.93S", "167.31E", "Vanuatu Islands"));
		testCases.add(new TestCase("10.62N", "86.18W", "Off Coast of Costa Rica"));
		testCases.add(new TestCase("30.30S", "177.53W", "Kermadec Islands, New Zealand"));
		testCases.add(new TestCase("8.25N", "126.71E", "Mindanao, Philippines"));
		testCases.add(new TestCase("26.18S", "178.55E", "South of Fiji Islands"));
		testCases.add(new TestCase("43.77N", "147.88E", "Kuril Islands"));
		testCases.add(new TestCase("28.58N", "128.79E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("36.54N", "71.07E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("38.83N", "26.31E", "Aegean Sea"));
		testCases.add(new TestCase("28.04S", "66.62W", "Catamarca Province, Argentina"));
		testCases.add(new TestCase("7.58S", "125.77E", "Banda Sea"));
		testCases.add(new TestCase("1.47N", "126.82E", "Northern Molucca Sea"));
		testCases.add(new TestCase("38.31N", "20.25E", "Greece"));
		testCases.add(new TestCase("52.76N", "160.37E", "Off East Coast of Kamchatka"));
		testCases.add(new TestCase("2.19N", "128.27E", "Halmahera, Indonesia"));
		testCases.add(new TestCase("39.07N", "21.81E", "Greece"));
		testCases.add(new TestCase("24.70S", "177.30W", "South of Fiji Islands"));
		testCases.add(new TestCase("37.38N", "48.85E", "Northwestern Iran"));
		testCases.add(new TestCase("37.60N", "20.81E", "Ionian Sea"));
		testCases.add(new TestCase("24.25S", "67.24W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("1.49S", "138.73E", "Near North Coast of Irian Jaya"));
		testCases.add(new TestCase("36.60N", "70.85E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("30.76S", "71.25W", "Near Coast of Central Chile"));
		testCases.add(new TestCase("9.79S", "124.66E", "Timor Region"));
		testCases.add(new TestCase("41.10N", "20.96E", "Macedonia"));
		testCases.add(new TestCase("38.25N", "20.28E", "Greece"));
		testCases.add(new TestCase("3.06N", "127.92E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("10.20N", "126.29E", "Philippine Islands Region"));
		testCases.add(new TestCase("36.58N", "83.52E", "Southern Xinjiang, China"));
		testCases.add(new TestCase("5.43N", "82.59W", "South of Panama"));
		testCases.add(new TestCase("38.82N", "26.43E", "Aegean Sea"));
		testCases.add(new TestCase("17.42S", "178.42W", "Fiji Islands Region"));
		testCases.add(new TestCase("69.00N", "17.18W", "Jan Mayen Island Region"));
		testCases.add(new TestCase("38.76N", "70.78E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("21.14S", "68.35W", "Chile-Bolivia Border Region"));
		testCases.add(new TestCase("39.62N", "73.57E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("36.44N", "70.78E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("42.35S", "72.21W", "Southern Chile"));
		testCases.add(new TestCase("38.94N", "141.77E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("5.82N", "126.13E", "Mindanao, Philippines"));
		testCases.add(new TestCase("19.36S", "176.24W", "Fiji Islands Region"));
		testCases.add(new TestCase("22.42S", "68.71W", "Northern Chile"));
		testCases.add(new TestCase("38.38N", "23.46E", "Greece"));
		testCases.add(new TestCase("41.20S", "90.53W", "Southeast of Easter Island"));
		testCases.add(new TestCase("52.03N", "170.94W", "Fox Islands, Aleutian Islands"));
		testCases.add(new TestCase("4.34N", "125.55E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("1.30S", "120.54E", "Sulawesi, Indonesia"));
		testCases.add(new TestCase("35.98N", "137.40E", "Eastern Honshu, Japan"));
		testCases.add(new TestCase("9.40S", "66.67E", "Mid Indian Ridge"));
		testCases.add(new TestCase("35.84N", "137.66E", "Eastern Honshu, Japan"));
		testCases.add(new TestCase("7.16S", "125.39E", "Banda Sea"));
		testCases.add(new TestCase("17.21N", "120.20E", "Luzon, Philippines"));
		testCases.add(new TestCase("26.54S", "177.73W", "South of Fiji Islands"));
		testCases.add(new TestCase("15.50S", "73.23W", "Southern Peru"));
		testCases.add(new TestCase("0.56N", "98.12E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("41.11S", "80.70E", "Mid Indian Ridge"));
		testCases.add(new TestCase("42.93N", "13.17E", "Central Italy"));
		testCases.add(new TestCase("41.21S", "80.85E", "Mid Indian Ridge"));
		testCases.add(new TestCase("19.56S", "34.43E", "Mozambique"));
		testCases.add(new TestCase("6.61N", "82.10W", "South of Panama"));
		testCases.add(new TestCase("23.29S", "176.78W", "South of Fiji Islands"));
		testCases.add(new TestCase("11.09S", "166.92E", "Santa Cruz Islands"));
		testCases.add(new TestCase("41.37N", "19.87E", "Albania"));
		testCases.add(new TestCase("0.90N", "123.11E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("39.35S", "71.78W", "S. Chile-Argentina Border Region"));
		testCases.add(new TestCase("6.58N", "82.27W", "South of Panama"));
		testCases.add(new TestCase("13.78N", "90.85W", "Near Coast of Guatemala"));
		testCases.add(new TestCase("4.47S", "129.33E", "Banda Sea"));
		testCases.add(new TestCase("39.35N", "142.03E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("6.96S", "126.84E", "Banda Sea"));
		testCases.add(new TestCase("46.19N", "152.29E", "Kuril Islands"));
		testCases.add(new TestCase("14.46N", "92.58W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("32.46N", "141.71E", "Southeast of Honshu, Japan"));
		testCases.add(new TestCase("6.55S", "130.10E", "Banda Sea"));
		testCases.add(new TestCase("17.19S", "173.74W", "Tonga Islands"));
		testCases.add(new TestCase("5.83S", "102.46E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("13.86N", "90.73W", "Near Coast of Guatemala"));
		testCases.add(new TestCase("2.25S", "140.31E", "Near North Coast of Irian Jaya"));
		testCases.add(new TestCase("26.18S", "178.53E", "South of Fiji Islands"));
		testCases.add(new TestCase("34.60N", "69.63E", "Southeastern Afghanistan"));
		testCases.add(new TestCase("38.55N", "27.50E", "Turkey"));
		testCases.add(new TestCase("47.95N", "146.10E", "Northwest of Kuril Islands"));
		testCases.add(new TestCase("23.35S", "179.78E", "South of Fiji Islands"));
		testCases.add(new TestCase("36.66N", "71.42E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("17.25N", "99.44W", "Guerrero, Mexico"));
		testCases.add(new TestCase("27.18N", "54.47E", "Southern Iran"));
		testCases.add(new TestCase("24.14S", "67.06W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("38.80N", "26.52E", "Aegean Sea"));
		testCases.add(new TestCase("45.10N", "149.80E", "Kuril Islands"));
		testCases.add(new TestCase("35.93N", "54.11E", "Northern and Central Iran"));
		testCases.add(new TestCase("12.98N", "145.82E", "South of Mariana Islands"));
		testCases.add(new TestCase("2.78N", "126.10E", "Northern Molucca Sea"));
		testCases.add(new TestCase("51.51N", "176.91W", "Andreanof Islands, Aleutian Islands"));
		testCases.add(new TestCase("10.37S", "161.24E", "Solomon Islands"));
		testCases.add(new TestCase("38.40N", "73.41E", "Tajikistan-Xinjiang Border Region"));
		testCases.add(new TestCase("52.22N", "179.50E", "Rat Islands, Aleutian Islands"));
		testCases.add(new TestCase("57.84N", "154.63W", "Kodiak Island Region, Alaska"));
		testCases.add(new TestCase("15.13S", "179.06W", "Fiji Islands Region"));
		testCases.add(new TestCase("11.21N", "143.06E", "South of Mariana Islands"));
		testCases.add(new TestCase("35.38N", "24.50E", "Crete, Greece"));
		testCases.add(new TestCase("36.65N", "70.93E", "Hindu Kush Region, Afghanistan"));
		testCases.add(new TestCase("24.61N", "121.97E", "Taiwan"));
		testCases.add(new TestCase("0.49N", "98.50E", "Northern Sumatra, Indonesia"));
		testCases.add(new TestCase("7.79S", "127.61E", "Banda Sea"));
		testCases.add(new TestCase("6.06S", "150.46E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("19.31N", "74.08W", "Cuba Region"));
		testCases.add(new TestCase("4.10S", "102.10E", "Southern Sumatra, Indonesia"));
		testCases.add(new TestCase("84.91N", "4.56E", "North of Svalbard"));
		testCases.add(new TestCase("2.39N", "128.67E", "Halmahera, Indonesia"));
		testCases.add(new TestCase("32.91N", "131.89E", "Kyushu, Japan"));
		testCases.add(new TestCase("21.03N", "122.36E", "Taiwan Region"));
		testCases.add(new TestCase("6.21S", "149.83E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("41.11N", "20.88E", "Macedonia"));
		testCases.add(new TestCase("56.10S", "27.51W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("56.06S", "26.54W", "South Sandwich Islands Region"));
		testCases.add(new TestCase("6.53S", "154.80E", "Solomon Islands"));
		testCases.add(new TestCase("7.95S", "122.82E", "Flores Sea"));
		testCases.add(new TestCase("6.21S", "130.58E", "Banda Sea"));
		testCases.add(new TestCase("51.56N", "178.44W", "Andreanof Islands, Aleutian Islands"));
		testCases.add(new TestCase("24.50S", "179.63W", "South of Fiji Islands"));
		testCases.add(new TestCase("47.56N", "13.62E", "Austria"));
		testCases.add(new TestCase("12.22N", "88.75W", "Off Coast of Central America"));
		testCases.add(new TestCase("1.85N", "126.34E", "Northern Molucca Sea"));
		testCases.add(new TestCase("44.34N", "11.54E", "Northern Italy"));
		testCases.add(new TestCase("46.10S", "75.56W", "Southern Chile"));
		testCases.add(new TestCase("24.21S", "67.00W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("10.78N", "57.32E", "Carlsberg Ridge"));
		testCases.add(new TestCase("14.46S", "167.33E", "Vanuatu Islands"));
		testCases.add(new TestCase("26.75N", "129.63E", "Ryukyu Islands, Japan"));
		testCases.add(new TestCase("21.19S", "179.09W", "Fiji Islands Region"));
		testCases.add(new TestCase("19.11S", "69.21W", "Northern Chile"));
		testCases.add(new TestCase("22.30N", "37.84E", "Red Sea"));
		testCases.add(new TestCase("20.73S", "176.64W", "Fiji Islands Region"));
		testCases.add(new TestCase("15.20S", "173.42W", "Tonga Islands"));
		testCases.add(new TestCase("38.02N", "21.10E", "Greece"));
		testCases.add(new TestCase("7.62S", "118.27E", "Flores Sea"));
		testCases.add(new TestCase("3.67S", "76.84W", "Northern Peru"));
		testCases.add(new TestCase("39.08N", "70.76E", "Tajikistan"));
		testCases.add(new TestCase("11.91N", "86.99W", "Near Coast of Nicaragua"));
		testCases.add(new TestCase("1.62N", "90.98E", "North Indian Ocean"));
		testCases.add(new TestCase("79.68N", "2.54E", "Greenland Sea"));
		testCases.add(new TestCase("39.56N", "23.28E", "Aegean Sea"));
		testCases.add(new TestCase("24.58N", "94.00E", "Myanmar-India Border Region"));
		testCases.add(new TestCase("3.60S", "131.27E", "Irian Jaya Region, Indonesia"));
		testCases.add(new TestCase("37.20N", "140.70E", "Eastern Honshu, Japan"));
		testCases.add(new TestCase("23.86S", "179.74W", "South of Fiji Islands"));
		testCases.add(new TestCase("29.97S", "177.62W", "Kermadec Islands, New Zealand"));
		testCases.add(new TestCase("3.55S", "131.24E", "Irian Jaya Region, Indonesia"));
		testCases.add(new TestCase("37.99N", "20.96E", "Ionian Sea"));
		testCases.add(new TestCase("25.48N", "142.73E", "Volcano Islands, Japan Region"));
		testCases.add(new TestCase("61.10N", "27.89W", "Iceland Region"));
		testCases.add(new TestCase("42.65N", "18.92E", "NW Balkan Region"));
		testCases.add(new TestCase("22.17S", "179.50W", "South of Fiji Islands"));
		testCases.add(new TestCase("41.12N", "20.91E", "Macedonia"));
		testCases.add(new TestCase("71.71N", "52.79W", "Western Greenland"));
		testCases.add(new TestCase("5.58S", "151.71E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("24.04S", "179.49E", "South of Fiji Islands"));
		testCases.add(new TestCase("3.76N", "63.87E", "Carlsberg Ridge"));
		testCases.add(new TestCase("52.88N", "171.19E", "Near Islands, Aleutian Islands"));
		testCases.add(new TestCase("38.86N", "26.50E", "Aegean Sea"));
		testCases.add(new TestCase("38.75N", "26.45E", "Aegean Sea"));
		testCases.add(new TestCase("3.81N", "127.92E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("16.13S", "167.96E", "Vanuatu Islands"));
		testCases.add(new TestCase("22.76S", "68.67W", "Northern Chile"));
		testCases.add(new TestCase("2.60S", "121.74E", "Sulawesi, Indonesia"));
		testCases.add(new TestCase("11.40N", "86.61W", "Near Coast of Nicaragua"));
		testCases.add(new TestCase("38.87N", "26.23E", "Aegean Sea"));
		testCases.add(new TestCase("4.70S", "153.20E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("51.77N", "173.44W", "Andreanof Islands, Aleutian Islands"));
		testCases.add(new TestCase("36.74N", "22.82E", "Southern Greece"));
		testCases.add(new TestCase("62.84S", "160.73W", "Pacific Antarctic Ridge"));
		testCases.add(new TestCase("27.81N", "139.61E", "Bonin Islands, Japan Region"));
		testCases.add(new TestCase("44.76N", "36.83E", "Crimea Region, Ukraine"));
		testCases.add(new TestCase("40.85N", "47.74E", "Eastern Caucasus"));
		testCases.add(new TestCase("38.86N", "26.47E", "Aegean Sea"));
		testCases.add(new TestCase("38.82N", "26.12E", "Aegean Sea"));
		testCases.add(new TestCase("30.98S", "70.72W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("3.26S", "146.96E", "Bismarck Sea"));
		testCases.add(new TestCase("38.88N", "26.30E", "Aegean Sea"));
		testCases.add(new TestCase("8.62N", "127.03E", "Philippine Islands Region"));
		testCases.add(new TestCase("44.85N", "110.99W", "Yellowstone Region, Wyoming"));
		testCases.add(new TestCase("4.22S", "142.66E", "New Guinea, Papua New Guinea"));
		testCases.add(new TestCase("37.14N", "71.35E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("34.41N", "6.82E", "Northern Algeria"));
		testCases.add(new TestCase("13.29N", "118.92E", "Philippine Islands Region"));
		testCases.add(new TestCase("16.55S", "72.33W", "Near Coast of Peru"));
		testCases.add(new TestCase("17.72S", "178.69W", "Fiji Islands Region"));
		testCases.add(new TestCase("0.34S", "132.34E", "Irian Jaya Region, Indonesia"));
		testCases.add(new TestCase("8.67S", "118.39E", "Sumbawa Region, Indonesia"));
		testCases.add(new TestCase("55.40S", "124.80W", "Southern East Pacific Rise"));
		testCases.add(new TestCase("38.87N", "26.34E", "Aegean Sea"));
		testCases.add(new TestCase("6.20S", "147.75E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("21.57S", "66.80W", "Southern Bolivia"));
		testCases.add(new TestCase("13.18N", "51.04E", "Eastern Gulf of Aden"));
		testCases.add(new TestCase("4.19N", "82.59W", "South of Panama"));
		testCases.add(new TestCase("19.69N", "122.71E", "Philippine Islands Region"));
		testCases.add(new TestCase("30.50S", "178.09W", "Kermadec Islands, New Zealand"));
		testCases.add(new TestCase("15.18N", "97.13W", "Near Coast of Oaxaca, Mexico"));
		testCases.add(new TestCase("14.19N", "92.24W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("56.51N", "168.37W", "Pribilof Islands, Alaska Region"));
		testCases.add(new TestCase("7.19S", "107.50W", "Central East Pacific Rise"));
		testCases.add(new TestCase("7.71S", "128.00E", "Banda Sea"));
		testCases.add(new TestCase("2.67N", "128.12E", "Halmahera, Indonesia"));
		testCases.add(new TestCase("34.86N", "72.89E", "Pakistan"));
		testCases.add(new TestCase("5.81N", "82.57W", "South of Panama"));
		testCases.add(new TestCase("14.94N", "91.80W", "Guatemala"));
		testCases.add(new TestCase("15.09N", "91.69W", "Mexico-Guatemala Border Region"));
		testCases.add(new TestCase("14.46N", "93.12W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("34.67N", "23.87E", "Crete, Greece"));
		testCases.add(new TestCase("14.17N", "93.12W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("48.28N", "154.08E", "Kuril Islands"));
		testCases.add(new TestCase("23.02S", "170.19E", "Southeast of Loyalty Islands"));
		testCases.add(new TestCase("27.99S", "67.27W", "Catamarca Province, Argentina"));
		testCases.add(new TestCase("38.90N", "26.45E", "Aegean Sea"));
		testCases.add(new TestCase("17.71S", "169.25E", "Vanuatu Islands"));
		testCases.add(new TestCase("37.16N", "134.97E", "Sea of Japan"));
		testCases.add(new TestCase("43.20N", "17.84E", "NW Balkan Region"));
		testCases.add(new TestCase("13.83N", "92.23W", "Off Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("33.81N", "91.05E", "Qinghai, China"));
		testCases.add(new TestCase("6.52S", "147.46E", "Eastern New Guinea Reg., P.N.G."));
		testCases.add(new TestCase("2.44N", "126.88E", "Northern Molucca Sea"));
		testCases.add(new TestCase("27.99N", "139.88E", "Bonin Islands, Japan Region"));
		testCases.add(new TestCase("2.00N", "123.83E", "Celebes Sea"));
		testCases.add(new TestCase("18.06S", "178.36W", "Fiji Islands Region"));
		testCases.add(new TestCase("13.93N", "92.12W", "Off Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("24.27S", "67.09W", "Chile-Argentina Border Region"));
		testCases.add(new TestCase("63.90N", "148.20W", "Central Alaska"));
		testCases.add(new TestCase("13.93N", "92.27W", "Off Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("5.42N", "125.18E", "Mindanao, Philippines"));
		testCases.add(new TestCase("38.24N", "72.62E", "Tajikistan"));
		testCases.add(new TestCase("38.82N", "26.39E", "Aegean Sea"));
		testCases.add(new TestCase("4.88S", "151.57E", "New Britain Region, P.N.G."));
		testCases.add(new TestCase("6.47S", "145.72E", "New Guinea, Papua New Guinea"));
		testCases.add(new TestCase("5.39S", "153.62E", "New Ireland Region, P.N.G."));
		testCases.add(new TestCase("14.55N", "92.68W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("35.67N", "140.30E", "Near East Coast of Honshu, Japan"));
		testCases.add(new TestCase("38.94N", "26.23E", "Aegean Sea"));
		testCases.add(new TestCase("5.60S", "129.77E", "Banda Sea"));
		testCases.add(new TestCase("36.10N", "71.39E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("38.87N", "26.31E", "Aegean Sea"));
		testCases.add(new TestCase("60.85N", "167.22E", "Eastern Siberia, Russia"));
		testCases.add(new TestCase("38.90N", "26.35E", "Aegean Sea"));
		testCases.add(new TestCase("36.49N", "71.29E", "Afghanistan-Tajikistan Border Region"));
		testCases.add(new TestCase("38.85N", "26.35E", "Aegean Sea"));
		testCases.add(new TestCase("8.81S", "119.20E", "Flores Region, Indonesia"));
		testCases.add(new TestCase("47.51N", "15.18E", "Austria"));
		testCases.add(new TestCase("43.28N", "142.50E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("3.79N", "126.73E", "Talaud Islands, Indonesia"));
		testCases.add(new TestCase("31.45S", "71.57W", "Near Coast of Central Chile"));
		testCases.add(new TestCase("8.09S", "106.45E", "South of Java, Indonesia"));
		testCases.add(new TestCase("21.13S", "68.80W", "Chile-Bolivia Border Region"));
		testCases.add(new TestCase("6.82S", "11.82W", "Ascension Island Region"));
		testCases.add(new TestCase("17.54N", "120.37E", "Luzon, Philippines"));
		testCases.add(new TestCase("10.89S", "162.09E", "Solomon Islands"));
		testCases.add(new TestCase("17.55S", "63.89W", "Central Bolivia"));
		testCases.add(new TestCase("14.78S", "167.57E", "Vanuatu Islands"));
		testCases.add(new TestCase("22.91S", "66.30W", "Jujuy Province, Argentina"));
		testCases.add(new TestCase("14.41N", "92.96W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("4.41S", "126.09E", "Banda Sea"));
		testCases.add(new TestCase("25.61N", "124.97E", "Northeast of Taiwan"));
		testCases.add(new TestCase("14.54N", "92.98W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("50.77N", "129.79W", "Vancouver Island, Canada Region"));
		testCases.add(new TestCase("14.34N", "92.96W", "Near Coast of Chiapas, Mexico"));
		testCases.add(new TestCase("11.52S", "166.39E", "Santa Cruz Islands"));
		testCases.add(new TestCase("4.23S", "126.84E", "Banda Sea"));
		testCases.add(new TestCase("47.48S", "13.57W", "Southern Mid Atlantic Ridge"));
		testCases.add(new TestCase("42.38N", "144.81E", "Hokkaido, Japan Region"));
		testCases.add(new TestCase("54.16S", "146.61W", "Pacific Antarctic Ridge"));
		testCases.add(new TestCase("0.31N", "124.30E", "Minahassa Peninsula, Sulawesi"));
		testCases.add(new TestCase("11.68N", "72.07W", "Near North Coast of Colombia"));
		testCases.add(new TestCase("80.32N", "0.90E", "North of Svalbard"));

		for (final TestCase tc : testCases) {
			final String geofon = tc.name.toUpperCase();
			final String expected = geofon.replace("VOGTLAND (GERMAN-CZECH BORDER REGION)", "GERMANY").replace("MACEDONIA", "ALBANIA").replace("NW BALKAN REGION", "NORTHWESTERN BALKAN REGION").replace("OFF WEST COAST OF NORTHERN SUMATRA", "OFF W COAST OF NORTHERN SUMATRA").replace('-', ' ').replace('/', ' ').replace(" ", "");

			final String feRegion = instance.getGeographicRegion(tc.lon, tc.lat).getName();
			final String actual = feRegion.replace('-', ' ').replace('/', ' ').replace(" ", "");

			Assertions.assertEquals(expected.substring(0, Math.min(expected.length(), 10)), actual.substring(0, Math.min(actual.length(), 10)), "lon: " + tc.lon + ", lat: " + tc.lat + ", expected: \"" + geofon + "\", actual: \"" + feRegion + "\"");
		}
	}

	@Test
	void testGetAllRegions() {
		final Map<Integer, Region> allRegions = instance.getAllGeographicRegions();
		Assertions.assertEquals(757, allRegions.size());
		log.debug("{}", allRegions);
	}

	@Test
	void testGetSeismicRegionNumber() {
		Assertions.assertEquals(1, instance.getSeismicRegionNumber(17));
		Assertions.assertEquals(30, instance.getSeismicRegionNumber(360));
		Assertions.assertEquals(50, instance.getSeismicRegionNumber(727));
		Assertions.assertEquals(44, instance.getSeismicRegionNumber(757));
	}

	@Test
	void testGetLatitudeLongitudeMap() {
		for (int i = 1; i <= 757; i++) {
			final Map<Integer, Set<LongitudeRange>> map = instance.getLatitudeLongitudeMap(i);
			log.debug("{} -> {}", i, map);
		}
		Assertions.assertTrue(true);
	}

	private void testGetName(final String arg0, final String arg1, final String expectedName) {
		final Region region = instance.getGeographicRegion(arg0, arg1);
		log.info("{}", region);
		Assertions.assertEquals(expectedName, region.getName(), "arg0: \"" + arg0 + "\", arg1: \"" + arg1 + '"');
	}

	@Value
	private static class TestCase {
		String lat;
		String lon;
		String name;

		private TestCase(@NonNull final String lat, @NonNull final String lon, @NonNull final String name) {
			this.lat = lat.trim();
			this.lon = lon.trim();
			this.name = name.trim();
		}
	}

}
