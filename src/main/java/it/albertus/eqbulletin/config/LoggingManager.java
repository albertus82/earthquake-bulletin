package it.albertus.eqbulletin.config;

import java.util.Objects;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import it.albertus.util.logging.ILoggingConfig;
import it.albertus.util.logging.ILoggingManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class LoggingManager implements ILoggingManager {

	@NonNull
	private final LoggingConfigAccessor currentConfig;

	private LoggingConfig previousConfig;

	@Override
	public void initializeLogging() {
		if (!equals(currentConfig, previousConfig)) {
			previousConfig = new LoggingConfig(currentConfig.getFileHandlerPattern(), currentConfig.isFileHandlerEnabled(), currentConfig.getFileHandlerLimit(), currentConfig.getFileHandlerCount(), currentConfig.getFileHandlerFormat(), currentConfig.getLoggingLevel());
			final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
			context.reset();
			try {
				new ContextInitializer(context).autoConfig();
			}
			catch (final JoranException e) {
				// StatusPrinter will handle this
			}
			StatusPrinter.printInCaseOfErrorsOrWarnings(context);
		}
	}

	private static boolean equals(final ILoggingConfig c1, final ILoggingConfig c2) {
		if (c1 == null && c2 == null) {
			return true;
		}
		if (c1 == null || c2 == null) {
			return false;
		}
		return c1.getFileHandlerCount() == c2.getFileHandlerCount() && Objects.equals(c1.getFileHandlerFormat(), c2.getFileHandlerFormat()) && c1.getFileHandlerLimit() == c2.getFileHandlerLimit() && Objects.equals(c1.getFileHandlerPattern(), c2.getFileHandlerPattern()) && Objects.equals(c1.getLoggingLevel(), c2.getLoggingLevel()) && c1.isFileHandlerEnabled() == c2.isFileHandlerEnabled();
	}

	@Value
	private class LoggingConfig implements ILoggingConfig {
		String fileHandlerPattern;
		boolean fileHandlerEnabled;
		int fileHandlerLimit;
		int fileHandlerCount;
		String fileHandlerFormat;
		String loggingLevel;
	}

}
