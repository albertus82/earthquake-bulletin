package io.github.albertus82.eqbulletin.service.net;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.Duration;

import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import io.github.albertus82.eqbulletin.gui.preference.Preference;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.decorators.Decorators.DecorateCheckedSupplier;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.vavr.CheckedFunction0;
import it.albertus.jface.preference.IPreferencesConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ResilientDownloader {

	protected final IPreferencesConfiguration configuration = EarthquakeBulletinConfig.getPreferencesConfiguration();

	protected InputStream connectionInputStream;

	private Retry getRetry() {
		return Retry.of(getClass().getSimpleName() + Retry.class.getSimpleName(), RetryConfig.custom().maxAttempts(configuration.getBoolean(Preference.PROXY_ENABLED, ConnectionFactory.Defaults.PROXY_ENABLED) && configuration.getBoolean(Preference.PROXY_AUTH_REQUIRED, ConnectionFactory.Defaults.PROXY_AUTH_REQUIRED) ? 1 : 3).waitDuration(Duration.ofSeconds(1)).ignoreExceptions(CancelException.class, MalformedURLException.class).build());
	}

	protected <T> DecorateCheckedSupplier<T> newResilientSupplier(final CheckedFunction0<T> supplier) {
		return Decorators.ofCheckedSupplier(supplier).withCircuitBreaker(NetCircuitBreaker.getInstance()).withRetry(getRetry());
	}

	public void cancel() {
		if (connectionInputStream != null) {
			try {
				connectionInputStream.close();
			}
			catch (final Exception e) {
				log.debug("Error closing the connection input stream:", e);
			}
		}
	}

}
