package gov.usgs.cr.hazards.feregion.fe_1995;

import java.util.Arrays;

import lombok.Value;

@Value
public class LongitudeRange {

	int from;
	int to;

	@Override
	public String toString() {
		return Arrays.toString(new int[] { from, to });
	}

}
