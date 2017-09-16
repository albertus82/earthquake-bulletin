package gov.usgs.cr.hazards.feregion.fe_1995;

public class IllegalCoordinateException extends IllegalArgumentException {

	private static final long serialVersionUID = -6026205341115756947L;

	IllegalCoordinateException(final String message) {
		super(message);
	}

	IllegalCoordinateException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
