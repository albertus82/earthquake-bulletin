package it.albertus.eqbulletin.model;

import java.io.Serializable;

public class MomentTensor implements Serializable {

	private static final long serialVersionUID = -6116085597261417308L;

	private final String text;
	private final String etag;

	public MomentTensor(final String text, final String etag) {
		this.text = text;
		this.etag = etag;
	}

	public String getText() {
		return text;
	}

	public String getEtag() {
		return etag;
	}

	@Override
	public String toString() {
		return "MomentTensor [etag=" + etag + "]";
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
		if (!(obj instanceof MomentTensor)) {
			return false;
		}
		final MomentTensor other = (MomentTensor) obj;
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
