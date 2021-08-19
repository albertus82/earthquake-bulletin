package it.albertus.eqbulletin.config.logging;

import ch.qos.logback.classic.Level;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LoggingLevel {

	OFF(Level.OFF),
	ERROR(Level.ERROR),
	WARN(Level.WARN),
	INFO(Level.INFO),
	DEBUG(Level.DEBUG),
	TRACE(Level.TRACE),
	ALL(Level.ALL);

	@NonNull
	private final Level level;

	/** Returns the string representation of this Level. */
	@Override
	public String toString() {
		return level.toString();
	}

}
