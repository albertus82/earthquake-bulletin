package it.albertus.eqbulletin.config;

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
		if (previousConfig == null) {
			previousConfig = new LoggingConfig(currentConfig);
		}
		else if (!new LoggingConfig(currentConfig).equals(previousConfig)) {
			previousConfig = new LoggingConfig(currentConfig);
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

	@Value
	private class LoggingConfig implements ILoggingConfig {
		String fileHandlerPattern;
		boolean fileHandlerEnabled;
		int fileHandlerLimit;
		int fileHandlerCount;
		String fileHandlerFormat;
		String loggingLevel;

		private LoggingConfig(@NonNull final LoggingConfigAccessor config) {
			this.fileHandlerPattern = config.getFileHandlerPattern();
			this.fileHandlerEnabled = config.isFileHandlerEnabled();
			this.fileHandlerLimit = config.getFileHandlerLimit();
			this.fileHandlerCount = config.getFileHandlerCount();
			this.fileHandlerFormat = config.getFileHandlerFormat();
			this.loggingLevel = config.getLoggingLevel();
		}
	}

}
