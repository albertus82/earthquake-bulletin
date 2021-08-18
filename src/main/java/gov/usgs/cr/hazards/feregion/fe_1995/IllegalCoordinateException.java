package gov.usgs.cr.hazards.feregion.fe_1995;

import lombok.Getter;

@Getter
public class IllegalCoordinateException extends IllegalArgumentException {

	private static final long serialVersionUID = 6432462192858607474L;

	private final String latitude;
	private final String longitude;

	IllegalCoordinateException(final Object latitude, final Object longitude) {
		super(String.format("Bad latitude or longitude: %s %s", latitude, longitude));
		this.latitude = latitude != null ? latitude.toString() : null;
		this.longitude = longitude != null ? longitude.toString() : null;
	}

	IllegalCoordinateException(final Object latitude, final Object longitude, final Throwable cause) {
		super(String.format("Bad latitude or longitude: %s %s", latitude, longitude), cause);
		this.latitude = latitude != null ? latitude.toString() : null;
		this.longitude = longitude != null ? longitude.toString() : null;
	}

}
