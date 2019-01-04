package it.albertus.eqbulletin.model;

import java.io.Serializable;
import java.util.Arrays;

public class MapImage implements Serializable {

	private static final long serialVersionUID = -9025202158647888544L;

	private final byte[] bytes;
	private final String etag;

	public MapImage(final byte[] bytes, final String etag) {
		this.bytes = bytes;
		this.etag = etag;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public String getEtag() {
		return etag;
	}

	@Override
	public String toString() {
		return "MapImage [etag=" + etag + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes);
		return result;
	}

	/**
	 * If both {@code etag} fields aren't null or empty, compares them, else
	 * compares the byte arrays ({@code bytes} fields).
	 *
	 * @param obj the reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj argument;
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MapImage)) {
			return false;
		}
		final MapImage other = (MapImage) obj;
		if (etag != null && !etag.isEmpty() && other.etag != null && !other.etag.isEmpty() && etag.equals(other.etag)) {
			return true;
		}
		return Arrays.equals(bytes, other.bytes);
	}

}
