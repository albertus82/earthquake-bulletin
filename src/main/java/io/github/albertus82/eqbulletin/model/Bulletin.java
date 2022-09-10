package io.github.albertus82.eqbulletin.model;

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
public class Bulletin {

	@NonNull
	@EqualsAndHashCode.Include
	private final Collection<Earthquake> events;
	@NonNull
	private final Instant instant;

	public Bulletin(final Collection<Earthquake> events) {
		this(events, Instant.now());
	}

}
