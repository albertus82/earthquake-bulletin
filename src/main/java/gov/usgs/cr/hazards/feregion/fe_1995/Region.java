package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.Serializable;

/**
 * This class represents a Flinn-Engdahl geographical region with its number and
 * name.
 */
public class Region implements Serializable, Comparable<Region> {

	private static final long serialVersionUID = -425945377738943037L;

	private final int number;
	private final String name;

	Region(final int number, final String name) {
		if (number < 1 || name.isEmpty()) { // may throw NPE
			throw new IllegalArgumentException();
		}
		this.number = number;
		this.name = name;
	}

	/**
	 * Returns the Flinn-Engdahl region number.
	 * 
	 * @return the region number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Returns the Flinn-Engdahl region name.
	 * 
	 * @return the region name
	 * @see #toString()
	 */
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + number;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
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
		return number == other.number;
	}

	/**
	 * Returns the Flinn-Engdahl region name.
	 * 
	 * @return the region name
	 * @see #getName()
	 */
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(final Region o) {
		return Integer.compare(number, o.number);
	}

}
