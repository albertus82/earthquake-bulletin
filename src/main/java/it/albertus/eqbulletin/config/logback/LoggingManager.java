package it.albertus.eqbulletin.config.logback;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
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
		Level consoleLevel;
		boolean fileCompressionEnabled;
		int fileHandlerCount;
		boolean fileHandlerEnabled;
		String fileHandlerFormat;
		int fileHandlerLimit;
		String fileHandlerPattern;
		Level fileLevel;
		String loggingLevel;

		private LoggingConfig(@NonNull final LoggingConfigAccessor config) {
			this.consoleLevel = config.getConsoleLevel();
			this.fileCompressionEnabled = config.isFileCompressionEnabled();
			this.fileHandlerCount = config.getFileHandlerCount();
			this.fileHandlerEnabled = config.isFileHandlerEnabled();
			this.fileHandlerFormat = config.getFileHandlerFormat();
			this.fileHandlerLimit = config.getFileHandlerLimit();
			this.fileHandlerPattern = config.getFileHandlerPattern();
			this.fileLevel = config.getFileLevel();
			this.loggingLevel = config.getLoggingLevel();
		}
	}

}
