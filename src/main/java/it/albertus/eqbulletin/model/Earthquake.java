package it.albertus.eqbulletin.model;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

public class Earthquake implements Serializable, Comparable<Earthquake> {

	private static final long serialVersionUID = -8121452375652796685L;

	private final String guid;
	private final Date time;
	private final float magnitude;
	private final Latitude latitude;
	private final Longitude longitude;
	private final Depth depth;
	private final Status status;
	private URL momentTensorUrl;
	private final String region;
	private final URL link;
	private final URL enclosureUrl;

	public Earthquake(String guid, Date time, float magnitude, Latitude latitude, Longitude longitude, Depth depth, Status status, String region, URL link, URL enclosureUrl) {
		this.guid = guid;
		this.time = time;
		this.magnitude = magnitude;
		this.latitude = latitude;
		this.longitude = longitude;
		this.depth = depth;
		this.status = status;
		this.region = region;
		this.link = link;
		this.enclosureUrl = enclosureUrl;
	}

	public String getGuid() {
		return guid;
	}

	public Date getTime() {
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

	public URL getMomentTensorUrl() {
		return momentTensorUrl;
	}

	public void setMomentTensorUrl(final URL momentTensorUrl) {
		this.momentTensorUrl = momentTensorUrl;
	}

	public String getRegion() {
		return region;
	}

	public URL getLink() {
		return link;
	}

	public URL getEnclosureUrl() {
		return enclosureUrl;
	}

	public String getGoogleMapsUrl() {
		return "https://maps.google.com/maps?q=" + Float.toString(Math.abs(latitude.getValue())) + (latitude.getValue() < 0 ? 'S' : 'N') + "," + Float.toString(Math.abs(longitude.getValue())) + (longitude.getValue() < 0 ? 'W' : 'E');
	}

	@Override
	public String toString() {
		return "Earthquake [guid=" + guid + ", time=" + time + ", magnitude=" + magnitude + ", latitude=" + latitude + ", longitude=" + longitude + ", depth=" + depth + ", status=" + status + ", momentTensorUrl=" + momentTensorUrl + ", region=" + region + ", link=" + link + ", enclosureUrl=" + enclosureUrl + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((guid == null) ? 0 : guid.hashCode());
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
		if (!(obj instanceof Earthquake)) {
			return false;
		}
		Earthquake other = (Earthquake) obj;
		if (guid == null) {
			if (other.guid != null) {
				return false;
			}
		}
		else if (!guid.equals(other.guid)) {
			return false;
		}
		return true;
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

}
