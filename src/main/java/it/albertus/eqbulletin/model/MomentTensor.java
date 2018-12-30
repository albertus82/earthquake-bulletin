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

}
