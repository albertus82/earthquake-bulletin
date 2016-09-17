package it.albertus.earthquake.model;

public enum Status {

	A("automatic"),
	C("confirmed"),
	M("manually revised");

	private final String description;

	Status(final String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
