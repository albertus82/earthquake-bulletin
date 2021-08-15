package it.albertus.eqbulletin.service.net;

import java.util.function.Supplier;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;

public enum NetCircuitBreaker implements Supplier<CircuitBreaker> {

	INSTANCE;

	private final CircuitBreaker circuitBreaker = CircuitBreaker.of(getClass().getSimpleName(), CircuitBreakerConfig.custom().ignoreExceptions(CancelException.class).build());

	@Override
	public CircuitBreaker get() {
		return circuitBreaker;
	}

	public static CircuitBreaker getInstance() {
		return INSTANCE.get();
	}

}
