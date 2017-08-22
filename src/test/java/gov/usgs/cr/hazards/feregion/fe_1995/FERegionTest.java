package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Test;

public class FERegionTest {

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

	private void test(final int step) throws IOException {
		try (final InputStream is = getClass().getResourceAsStream(String.format("feregion_%d_%d.txt.gz", step, step)); final GZIPInputStream gzis = new GZIPInputStream(is); final InputStreamReader isr = new InputStreamReader(gzis); final BufferedReader br = new BufferedReader(isr)) {
			for (int i = -180; i <= 180; i += step) {
				for (int j = -90; j <= 90; j += step) {
					final String name = FERegion.getName(i, j);
					Assert.assertEquals(i + ", " + j, name, br.readLine());
				}
				System.out.print(".");
			}
		}
	}

}
