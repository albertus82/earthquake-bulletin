package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FERegionTest {

	private static FERegion feRegion;

	@BeforeClass
	public static void init() throws IOException {
		feRegion = new FERegion();
	}

	@Test
	public void test_5_5() throws IOException {
		test(5);
	}

	@Test
	public void test_3_3() throws IOException {
		test(3);
	}

	@Test
	public void test_2_2() throws IOException {
		test(2);
	}

	@Test
	public void testCli() throws IOException {
		final PrintStream backup = System.out;
		try (final ByteArrayOutputStream out = new ByteArrayOutputStream(); final PrintStream ps = new PrintStream(out)) {
			System.setOut(ps);

			FERegion.main(new String[] { "12", "42" });
			ps.flush();
			Assert.assertEquals("CENTRAL ITALY", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "+12", "+42" });
			ps.flush();
			Assert.assertEquals("CENTRAL ITALY", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "12E", "42N" });
			ps.flush();
			Assert.assertEquals("CENTRAL ITALY", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "42N", "12E" });
			ps.flush();
			Assert.assertEquals("CENTRAL ITALY", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "42S", "12E" });
			ps.flush();
			Assert.assertEquals("SOUTHWEST OF AFRICA", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "12E", "42S" });
			ps.flush();
			Assert.assertEquals("SOUTHWEST OF AFRICA", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "12", "-42" });
			ps.flush();
			Assert.assertEquals("SOUTHWEST OF AFRICA", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "-42", "12" });
			ps.flush();
			Assert.assertEquals("NORTH ATLANTIC OCEAN", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "42W", "12" });
			ps.flush();
			Assert.assertEquals("NORTH ATLANTIC OCEAN", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "42W", "12N" });
			ps.flush();
			Assert.assertEquals("NORTH ATLANTIC OCEAN", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "-12", "-42" });
			ps.flush();
			Assert.assertEquals("TRISTAN DA CUNHA REGION", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "12W", "42S" });
			ps.flush();
			Assert.assertEquals("TRISTAN DA CUNHA REGION", out.toString().trim().toUpperCase());
			out.reset();

			FERegion.main(new String[] { "42S", "12W" });
			ps.flush();
			Assert.assertEquals("TRISTAN DA CUNHA REGION", out.toString().trim().toUpperCase());
			out.reset();
		}
		finally {
			System.setOut(backup);
		}
	}

	private void test(final int step) throws IOException {
		try (final InputStream is = getClass().getResourceAsStream(String.format("feregion_%d_%d.txt.gz", step, step)); final GZIPInputStream gzis = new GZIPInputStream(is); final InputStreamReader isr = new InputStreamReader(gzis); final BufferedReader br = new BufferedReader(isr)) {
			for (int i = -180; i <= 180; i += step) {
				for (int j = -90; j <= 90; j += step) {
					final String name = feRegion.getName(i, j);
					Assert.assertEquals(i + ", " + j, br.readLine().toUpperCase(), name.toUpperCase());
				}
				System.out.print(".");
			}
		}
		System.out.println();
	}

}
