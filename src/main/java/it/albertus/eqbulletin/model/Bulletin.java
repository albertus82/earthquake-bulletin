package it.albertus.eqbulletin.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

public class Bulletin implements Serializable {

	private static final long serialVersionUID = 4656384045849863383L;

	private final Collection<Earthquake> events;
	private final Instant instant;

	public Bulletin(final Collection<Earthquake> events) {
		this(events, Instant.now());
	}

	private Bulletin(final Collection<Earthquake> events, final Instant instant) {
		Objects.requireNonNull(events);
		Objects.requireNonNull(instant);
		this.events = events;
		this.instant = instant;
	}

	public Collection<Earthquake> getEvents() {
		return events;
	}

	public Instant getInstant() {
		return instant;
	}

	@Override
	public int hashCode() {
		return events.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Bulletin)) {
			return false;
		}
		final Bulletin other = (Bulletin) obj;
		return Objects.equals(events, other.events);
	}

}
