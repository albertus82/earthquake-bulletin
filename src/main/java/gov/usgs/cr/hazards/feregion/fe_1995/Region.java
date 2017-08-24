package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.Serializable;

import it.albertus.util.WordUtils;

public class Region implements Serializable {

	private static final long serialVersionUID = 5426148325885082610L;

	private final int number;
	private final String name;

	public Region(final int number, final String name) {
		if (number < 1 || name.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.number = number;
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public String getName(final boolean uppercase) {
		return uppercase ? name : WordUtils.capitalize(name.toLowerCase(), ' ', '-', '.').replace(" Of ", " of "); // Improved text case.
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Region)) {
			return false;
		}
		Region other = (Region) obj;
		if (number != other.number) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Region [number=" + number + ", name=" + name + "]";
	}

}
