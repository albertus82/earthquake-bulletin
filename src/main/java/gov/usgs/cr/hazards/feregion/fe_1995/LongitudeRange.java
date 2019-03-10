package gov.usgs.cr.hazards.feregion.fe_1995;

import java.util.Arrays;
import java.util.Objects;

public class LongitudeRange {

	private final int from;
	private final int to;

	public LongitudeRange(final int from, final int to) {
		this.from = from;
		this.to = to;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof LongitudeRange)) {
			return false;
		}
		final LongitudeRange other = (LongitudeRange) obj;
		return from == other.from && to == other.to;
	}

	@Override
	public String toString() {
		return Arrays.asList(from, to).toString();
	}

}
