package com.github.albertus82.eqbulletin.model;

import java.io.Serializable;
import java.net.URI;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import it.albertus.jface.maps.CoordinateUtils;
import lombok.NonNull;
import lombok.Value;

@Value
public class Earthquake implements Serializable, Comparable<Earthquake> {

	private static final long serialVersionUID = -6959335170582499256L;

	// @formatter:off
	@NonNull String guid;
	@NonNull ZonedDateTime time;
	float magnitude;
	@NonNull Latitude latitude;
	@NonNull Longitude longitude;
	@NonNull Depth depth;
	Status status; // status is nullable because it was removed from the HTML page on 2019-10-08 but it's present in the RSS feed
	@NonNull String region;
	URI link;
	URI enclosureUri;
	URI momentTensorUri;
	// @formatter:on

	public Optional<Status> getStatus() {
		return Optional.ofNullable(status);
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
		final NumberFormat numberFormat = CoordinateUtils.newFormatter();
		final StringBuilder details = new StringBuilder(dateTimeFormatter.format(time)).append(' ').append(latitude.toString(numberFormat)).append(' ').append(longitude.toString(numberFormat)).append(' ').append(depth);
		if (status != null) {
			details.append(' ').append(status);
		}
		return details.toString();
	}

	private String getDetails() {
		return getDetails(ZoneOffset.UTC);
	}

}
