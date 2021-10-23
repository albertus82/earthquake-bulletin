package com.github.albertus82.eqbulletin.config.logging;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import it.albertus.util.logging.ILoggingManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
public class LoggingManager implements ILoggingManager {

	@NonNull
	private final LoggingConfigAccessor currentConfig;

	private LoggingConfigValue previousConfig;

	@Override
	public void initializeLogging() {
		if (previousConfig == null) {
			previousConfig = new LoggingConfigValue(currentConfig);
		}
		else if (!new LoggingConfigValue(currentConfig).equals(previousConfig)) {
			previousConfig = new LoggingConfigValue(currentConfig);
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
	private class LoggingConfigValue implements LoggingConfig {
		Level consoleLevel;
		boolean fileAppenderEnabled;
		boolean fileCompressionEnabled;
		Level fileLevel;
		byte fileMaxIndex;
		int fileMaxSize;
		String fileNamePattern;
		String layoutPattern;
		Level rootLevel;

		private LoggingConfigValue(@NonNull final LoggingConfigAccessor config) {
			this.consoleLevel = config.getConsoleLevel();
			this.fileLevel = config.getFileLevel();
			this.fileMaxIndex = config.getFileMaxIndex();
			this.fileMaxSize = config.getFileMaxSize();
			this.fileNamePattern = config.getFileNamePattern();
			this.layoutPattern = config.getLayoutPattern();
			this.rootLevel = config.getRootLevel();
			this.fileAppenderEnabled = config.isFileAppenderEnabled();
			this.fileCompressionEnabled = config.isFileCompressionEnabled();
		}
	}

}
