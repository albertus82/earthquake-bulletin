package com.github.albertus82.eqbulletin.model;

import com.github.albertus82.eqbulletin.service.net.StaticBinaryResource;

public class MapImage extends StaticBinaryResource {

	private static final long serialVersionUID = 7536639013020954349L;

	public MapImage(final byte[] bytes, final String etag) {
		super(bytes, etag);
	}

}
