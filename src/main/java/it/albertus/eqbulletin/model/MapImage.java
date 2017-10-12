package it.albertus.eqbulletin.model;

import java.io.Serializable;

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

}
