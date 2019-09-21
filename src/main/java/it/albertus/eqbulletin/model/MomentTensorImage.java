package it.albertus.eqbulletin.model;

import it.albertus.eqbulletin.service.net.StaticBinaryResource;

public class MomentTensorImage extends StaticBinaryResource {

	private static final long serialVersionUID = 4147911522978502502L;

	public MomentTensorImage(final byte[] bytes, final String etag) {
		super(bytes, etag);
	}

}
