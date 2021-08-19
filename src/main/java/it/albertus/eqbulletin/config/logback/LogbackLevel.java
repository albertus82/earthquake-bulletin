package it.albertus.eqbulletin.config.logback;

import java.util.function.Supplier;

import ch.qos.logback.classic.Level;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LogbackLevel implements Supplier<Level> {

	OFF(Level.OFF),
	ERROR(Level.ERROR),
	WARN(Level.WARN),
	INFO(Level.INFO),
	DEBUG(Level.DEBUG),
	TRACE(Level.TRACE),
	ALL(Level.ALL);

	private final Level level;

	@Override
	public Level get() {
		return level;
	}

}
