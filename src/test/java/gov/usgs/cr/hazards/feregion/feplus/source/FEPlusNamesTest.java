package gov.usgs.cr.hazards.feregion.feplus.source;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class FEPlusNamesTest {

	private static FEPlusNames fenames;

	@BeforeClass
	public static void init() throws IOException {
		fenames = new FEPlusNames();
	}

	@Test
	public void test() {
		final Map<Integer, Map<FEPlusNameType, String>> names = fenames.getNameMap();
		Assert.assertEquals(757, names.size());
		for (final Entry<Integer, Map<FEPlusNameType, String>> entry : names.entrySet()) {
			Assert.assertEquals(FEPlusNameType.values().length, entry.getValue().size());
			for (final String s : entry.getValue().values()) {
				Assert.assertNotNull(s);
				Assert.assertNotEquals("", s);
			}
		}
	}

}
