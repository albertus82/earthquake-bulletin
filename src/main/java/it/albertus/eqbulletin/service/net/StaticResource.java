package it.albertus.eqbulletin.service.net;

public abstract class StaticResource implements Cacheable {

	private static final long serialVersionUID = 5238198279498087722L;

	protected final String etag;

	protected StaticResource(final String etag) {
		this.etag = etag;
	}

	@Override
	public String getEtag() {
		return etag;
	}

	@Override
	public String toString() {
		return "StaticResource [etag=" + etag + "]";
	}

}
