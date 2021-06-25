package gov.usgs.cr.hazards.feregion.feplus.source;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FEPlusNamesTest {

	private static FEPlusNames fenames;

	@BeforeAll
	static void init() throws IOException {
		fenames = new FEPlusNames();
	}

	@Test
	void test() {
		final Map<Integer, Map<FEPlusNameType, String>> names = fenames.getNameMap();
		Assertions.assertEquals(757, names.size());
		for (final Entry<Integer, Map<FEPlusNameType, String>> entry : names.entrySet()) {
			Assertions.assertEquals(FEPlusNameType.values().length, entry.getValue().size());
			for (final String s : entry.getValue().values()) {
				Assertions.assertNotNull(s);
				Assertions.assertNotEquals("", s);
			}
		}
	}

}
