package it.albertus.eqbulletin.model;

import java.io.Serializable;
import java.net.URI;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class Earthquake implements Serializable, Comparable<Earthquake> {

	private static final long serialVersionUID = -6959335170582499256L;

	private final String guid;
	private final ZonedDateTime time;
	private final float magnitude;
	private final Latitude latitude;
	private final Longitude longitude;
	private final Depth depth;
	private final Status status;
	private final String region;
	private final URI link;
	private final URI enclosureUri;
	private final URI momentTensorUri;

	public Earthquake(final String guid, final ZonedDateTime time, final float magnitude, final Latitude latitude, final Longitude longitude, final Depth depth, final Status status, final String region, final URI link, final URI enclosureUri, final URI momentTensorUri) {
		Objects.requireNonNull(guid);
		Objects.requireNonNull(time);
		Objects.requireNonNull(latitude);
		Objects.requireNonNull(longitude);
		Objects.requireNonNull(depth);
		Objects.requireNonNull(status);
		Objects.requireNonNull(region);
		this.guid = guid;
		this.time = time;
		this.magnitude = magnitude;
		this.latitude = latitude;
		this.longitude = longitude;
		this.depth = depth;
		this.status = status;
		this.region = region;
		this.link = link;
		this.enclosureUri = enclosureUri;
		this.momentTensorUri = momentTensorUri;
	}

	public String getGuid() {
		return guid;
	}

	public ZonedDateTime getTime() {
		return time;
	}

	public float getMagnitude() {
		return magnitude;
	}

	public Latitude getLatitude() {
		return latitude;
	}

	public Longitude getLongitude() {
		return longitude;
	}

	public Depth getDepth() {
		return depth;
	}

	public Status getStatus() {
		return status;
	}

	public String getRegion() {
		return region;
	}

	public Optional<URI> getLink() {
		return Optional.ofNullable(link);
	}

	public Optional<URI> getEnclosureUri() {
		return Optional.ofNullable(enclosureUri);
	}

	public Optional<URI> getMomentTensorUri() {
		return Optional.ofNullable(momentTensorUri);
	}

	@Override
	public int hashCode() {
		return Objects.hash(depth, enclosureUri, guid, latitude, link, longitude, magnitude, momentTensorUri, region, status, time);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Earthquake)) {
			return false;
		}
		final Earthquake other = (Earthquake) obj;
		return Objects.equals(depth, other.depth) && Objects.equals(enclosureUri, other.enclosureUri) && Objects.equals(guid, other.guid) && Objects.equals(latitude, other.latitude) && Objects.equals(link, other.link) && Objects.equals(longitude, other.longitude) && Float.floatToIntBits(magnitude) == Float.floatToIntBits(other.magnitude) && Objects.equals(momentTensorUri, other.momentTensorUri) && Objects.equals(region, other.region) && status == other.status && Objects.equals(time, other.time);
	}

	/** The natural order is by time descending */
	@Override
	public int compareTo(final Earthquake o) {
		if (this.equals(o)) {
			return 0;
		}
		else {
			return o.time.compareTo(this.time);
		}
	}

	@Override
	public String toString() {
		return "Earthquake [guid=" + guid + ", summary=" + getSummary() + ", details=" + getDetails() + "]";
	}

	public String getSummary() {
		return new StringBuilder("M ").append(magnitude).append(", ").append(region).toString().trim();
	}

	public String getDetails(final ZoneId timeZone) {
		final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(timeZone);
		return new StringBuilder(dateTimeFormatter.format(time)).append(' ').append(latitude).append(' ').append(longitude).append(' ').append(depth).append(' ').append(status).toString();
	}

	private String getDetails() {
		return getDetails(ZoneOffset.UTC);
	}

}
