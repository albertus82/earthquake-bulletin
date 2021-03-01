package gov.usgs.cr.hazards.feregion.fe_1995;

import static it.albertus.jface.maps.CoordinateUtils.DEGREE_SIGN;

import java.io.Serializable;
import java.util.Locale;

/**
 * This class represents geographical coordinates (longitude & latitude) in
 * decimal degrees.
 * <p>
 * Originally written by Bob Simpson in Perl language, with fix supplied by
 * George Randall (<tt>feregion.pl</tt>).
 * 
 * @see <a href="ftp://hazards.cr.usgs.gov/feregion/fe_1995/">1995 (latest)
 *      revision of the Flinn-Engdahl (F-E) seismic and geographical
 *      regionalization scheme and programs</a>
 */
public class Coordinates implements Serializable {

	private static final long serialVersionUID = 592347022653444918L;

	private final double longitude;
	private final double latitude;

	/**
	 * Constructs a new {@code Coordinates} object given <em>longitude</em> and
	 * <em>latitude</em> decimal values.
	 * 
	 * @param lng the longitude decimal value (e.g., 129, 129.524)
	 * @param lat the latitude decimal value (e.g., -42, -42.89)
	 * @throws IllegalCoordinateException if the arguments provided are invalid
	 */
	public Coordinates(double lng, final double lat) {
		// Adjust lat-lon values...
		if (lng <= -180.0) {
			lng += 360.0;
		}
		if (lng > 180.0) {
			lng -= 360.0;
		}

		if (Math.abs(lat) > 90.0 || Math.abs(lng) > 180.0) {
			throw new IllegalCoordinateException(String.format(" * bad latitude or longitude: %f %f", lat, lng));
		}

		this.longitude = lng;
		this.latitude = lat;
	}

	/**
	 * Given <em>longitude</em> and <em>latitude</em> decimal string values,
	 * returns a new {@code Coordinates} object.
	 * 
	 * @param longitude the longitude decimal string value (e.g., 129, 129.524,
	 *        129.524E)
	 * @param latitude the latitude decimal string value (e.g., -42, -42.89,
	 *        42.89S)
	 * @return a new {@code Coordinates} object built with the provided
	 *         geographical coordinates
	 * @throws IllegalCoordinateException if the provided arguments are invalid
	 */
	public static Coordinates parse(String longitude, String latitude) {
		longitude = longitude.toUpperCase(Locale.ROOT);
		latitude = latitude.toUpperCase(Locale.ROOT);

		// Allow for NSEW and switching of arguments.
		if (longitude.endsWith("N") || longitude.endsWith("S")) {
			final String tmp = longitude;
			longitude = latitude;
			latitude = tmp;
		}
		if (longitude.endsWith("W")) {
			longitude = '-' + longitude;
		}
		if (latitude.endsWith("S")) {
			latitude = '-' + latitude;
		}
		longitude = longitude.replaceAll("[EW]", "");
		latitude = latitude.replaceAll("[NS]", "");

		final double lng;
		final double lat;
		try {
			lng = Double.parseDouble(longitude);
			lat = Double.parseDouble(latitude);
		}
		catch (final NumberFormatException e) {
			throw new IllegalCoordinateException(String.format(" * bad latitude or longitude: %s %s", latitude, longitude), e);
		}

		return new Coordinates(lng, lat);
	}

	/**
	 * Returns the longitude coordinate.
	 * 
	 * @return the longitude value
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the latitude coordinate.
	 * 
	 * @return the latitude value
	 */
	public double getLatitude() {
		return latitude;
	}

	/** Returns a string representation of the geographical coordinates. */
	@Override
	public String toString() {
		return Double.toString(Math.abs(latitude)) + DEGREE_SIGN + (latitude < 0 ? 'S' : 'N') + ' ' + Math.abs(longitude) + DEGREE_SIGN + (longitude < 0 ? 'W' : 'E');
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		if (!(obj instanceof Coordinates)) {
			return false;
		}
		Coordinates other = (Coordinates) obj;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude)) {
			return false;
		}
		return Double.doubleToLongBits(longitude) == Double.doubleToLongBits(other.longitude);
	}

}
