package gov.usgs.cr.hazards.feregion.feplus.source;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FENamesTest {

	private static FENames fenames;

	@BeforeClass
	public static void init() throws IOException {
		fenames = new FENames();
	}

	@Test
	public void test() {
		final Map<Integer, Map<FENameType, String>> names = fenames.getNames();
		Assert.assertEquals(757, names.size());
		for (final Entry<Integer, Map<FENameType, String>> entry : names.entrySet()) {
			Assert.assertEquals(FENameType.values().length, entry.getValue().size());
			for (final String s : entry.getValue().values()) {
				Assert.assertNotNull(s);
				Assert.assertNotEquals("", s);
			}
		}
	}

}
