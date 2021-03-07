package it.albertus.eqbulletin.service.net;

import java.util.Arrays;

import lombok.Getter;

@Getter
public class StaticBinaryResource extends StaticResource {

	private static final long serialVersionUID = -6680655185888306718L;

	private final byte[] bytes;

	protected StaticBinaryResource(final byte[] bytes, final String etag) {
		super(etag);
		this.bytes = bytes;
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
		if (!(obj instanceof StaticBinaryResource)) {
			return false;
		}
		final StaticBinaryResource other = (StaticBinaryResource) obj;
		if (etag != null && !etag.isEmpty() && other.getEtag() != null && !other.getEtag().isEmpty() && etag.equals(other.getEtag())) {
			return true;
		}
		return Arrays.equals(bytes, other.bytes);
	}

}
