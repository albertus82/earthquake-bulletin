package gov.usgs.cr.hazards.feregion.feplus.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

public class FEPlusNames {

	private final Map<Integer, Map<FEPlusNameType, String>> names = new TreeMap<>();

	/**
	 * Initializes the internal names database. Reuse the same instance whenever
	 * possible.
	 */
	public FEPlusNames() throws IOException {
		try (final InputStream is = getClass().getResourceAsStream("fenames.asc"); final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.US_ASCII); final BufferedReader br = new BufferedReader(isr)) {
			Integer fenum = null;
			String line;
			while ((line = br.readLine()) != null) {
				final StringBuilder sb = new StringBuilder(80);
				for (int i = 0; i < line.length(); i++) {
					final char c = line.charAt(i);
					if (c == '!') {
						break; // discard comments
					}
					sb.append(c);
				}
				if (sb.length() != 0) {
					final FEPlusNameType type = FEPlusNameType.valueOf(sb.substring(0, 1));
					if (FEPlusNameType.S.equals(type)) {
						final Map<FEPlusNameType, String> value = new EnumMap<>(FEPlusNameType.class);
						value.put(type, sb.substring(6).trim());
						fenum = Integer.valueOf(sb.substring(2, 5).trim());
						names.put(fenum, value);
					}
					else {
						names.get(fenum).put(type, sb.substring(6).trim());
					}
				}
			}
		}
	}

	public Map<Integer, Map<FEPlusNameType, String>> getNames() {
		return names;
	}

}
