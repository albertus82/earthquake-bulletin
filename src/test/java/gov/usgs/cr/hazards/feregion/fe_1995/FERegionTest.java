package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Test;

import it.albertus.util.logging.LoggerFactory;

public class FERegionTest {

	private static final Logger logger = LoggerFactory.getLogger(FERegionTest.class);

	@Test
	public void test_5_5() throws IOException {
		final int step = 5;
		try (final InputStream is = getClass().getResourceAsStream(String.format("feregion_%d_%d.txt.gz", step, step)); final GZIPInputStream gzis = new GZIPInputStream(is); final InputStreamReader isr = new InputStreamReader(gzis); final BufferedReader br = new BufferedReader(isr)) {
			for (int i = -180; i <= 180; i += step) {
				for (int j = -90; j <= 90; j += step) {
					final String name = FERegion.getName(i, j);
					logger.log(Level.INFO, "{0}, {1}: {2}", new Object[] { i, j, name });
					Assert.assertEquals(name, br.readLine());
				}
			}
		}
	}

	@Test
	public void test_3_3() throws IOException {
		final int step = 3;
		try (final InputStream is = getClass().getResourceAsStream(String.format("feregion_%d_%d.txt.gz", step, step)); final GZIPInputStream gzis = new GZIPInputStream(is); final InputStreamReader isr = new InputStreamReader(gzis); final BufferedReader br = new BufferedReader(isr)) {
			for (int i = -180; i <= 180; i += step) {
				for (int j = -90; j <= 90; j += step) {
					final String name = FERegion.getName(i, j);
					logger.log(Level.INFO, "{0}, {1}: {2}", new Object[] { i, j, name });
					Assert.assertEquals(name, br.readLine());
				}
			}
		}
	}

}
