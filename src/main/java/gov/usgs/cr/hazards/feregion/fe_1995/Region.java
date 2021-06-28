package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

/**
 * This class represents a Flinn-Engdahl geographical region with its number and
 * name.
 * 
 * @see <a href=
 *      "https://earthquake.usgs.gov/learn/topics/flinn_engdahl_list.php">F-E
 *      Regions List</a>
 */
@Value
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Region implements Serializable, Comparable<Region> {

	private static final long serialVersionUID = -425945377738943037L;

	/** The Flinn-Engdahl region number */
	@EqualsAndHashCode.Include
	int number;

	/** The Flinn-Engdahl region name */
	String name;

	Region(final int number, @NonNull final String name) {
		if (number < 1 || name.isEmpty()) {
			throw new IllegalArgumentException();
		}
		this.number = number;
		this.name = name;
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
