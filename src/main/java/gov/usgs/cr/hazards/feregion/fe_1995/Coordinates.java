package gov.usgs.cr.hazards.feregion.fe_1995;

import java.io.Serializable;

public class Coordinates implements Serializable {

	private static final long serialVersionUID = -5672208470783902289L;

	private final double longitude;
	private final double latitude;

	public Coordinates(double lng, final double lat) throws IllegalCoordinateException {
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

	public static Coordinates parse(String longitude, String latitude) throws IllegalCoordinateException {
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
		longitude = longitude.replaceAll("E|W", "");
		latitude = latitude.replaceAll("N|S", "");

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

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return Double.toString(Math.abs(longitude)) + '\u00B0' + (longitude > 0 ? 'E' : 'W') + ' ' + Math.abs(latitude) + '\u00B0' + (latitude > 0 ? 'N' : 'S');
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
