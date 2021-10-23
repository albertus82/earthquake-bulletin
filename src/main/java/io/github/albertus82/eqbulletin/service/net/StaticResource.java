package io.github.albertus82.eqbulletin.service.net;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class StaticResource implements Cacheable {

	private static final long serialVersionUID = 5238198279498087722L;

	protected final String etag;

	@Override
	public String toString() {
		return "StaticResource [etag=" + etag + "]";
	}

}
