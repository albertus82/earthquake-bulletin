package it.albertus.eqbulletin.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Bulletin implements Serializable {

	private static final long serialVersionUID = 4656384045849863383L;

	@NonNull
	@EqualsAndHashCode.Include
	private final Collection<Earthquake> events;
	@NonNull
	private final Instant instant;

	public Bulletin(final Collection<Earthquake> events) {
		this(events, Instant.now());
	}

}
