package com.github.albertus82.eqbulletin.service.net;

import lombok.Getter;

@Getter
public class StaticTextResource extends StaticResource {

	private static final long serialVersionUID = 8206511103979270982L;

	private final String text;

	protected StaticTextResource(final String text, final String etag) {
		super(etag);
		this.text = text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		if (!(obj instanceof StaticTextResource)) {
			return false;
		}
		final StaticTextResource other = (StaticTextResource) obj;
		final String etag = getEtag();
		if (etag != null && !etag.isEmpty() && other.etag != null && !other.etag.isEmpty() && etag.equals(other.etag)) {
			return true;
		}
		if (text == null) {
			if (other.text != null) {
				return false;
			}
		}
		else if (!text.equals(other.text)) {
			return false;
		}
		return true;
	}

}
