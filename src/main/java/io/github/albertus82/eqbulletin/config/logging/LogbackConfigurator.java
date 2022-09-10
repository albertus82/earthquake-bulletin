package io.github.albertus82.eqbulletin.config.logging;

import org.eclipse.jface.util.Util;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.jul.LevelChangePropagator;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.util.FileSize;
import io.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LogbackConfigurator extends ContextAwareBase implements Configurator {

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger(); // Remove existing handlers attached to JUL root logger
		SLF4JBridgeHandler.install(); // Add SLF4JBridgeHandler to JUL's root logger
	}

	private final LoggingConfig config = new LoggingConfigAccessor(EarthquakeBulletinConfig.getPreferencesConfiguration());

	@Override
	public ExecutionStatus configure(final LoggerContext context) {
		addInfo("Reloading logging configuration...");

		final ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
		consoleAppender.setContext(context);
		consoleAppender.setName("consoleAppender");

		final LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
		encoder.setContext(context);
		final PatternLayout layout = new PatternLayout();
		layout.setPattern(config.getLayoutPattern());
		layout.setContext(context);
		layout.start();
		encoder.setLayout(layout);
		consoleAppender.setEncoder(encoder);

		final ThresholdFilter consoleThresholdFilter = new ThresholdFilter();
		consoleThresholdFilter.setContext(context);
		consoleThresholdFilter.setLevel(config.getConsoleLevel().toString());
		consoleAppender.addFilter(consoleThresholdFilter);
		consoleThresholdFilter.start();

		consoleAppender.start();

		final Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME); // NOSONAR Use static access with "org.slf4j.Logger" for "ROOT_LOGGER_NAME". "static" base class members should not be accessed via derived types (java:S3252)

		if (config.isFileAppenderEnabled()) {
			final RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
			rollingFileAppender.setContext(context);
			rollingFileAppender.setName("rollingFileAppender");
			rollingFileAppender.setEncoder(encoder);
			rollingFileAppender.setAppend(true);
			rollingFileAppender.setFile(config.getFileNamePattern().replace("%i", "0")); // The current file is the one with the zero index

			final FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
			fixedWindowRollingPolicy.setContext(context);
			fixedWindowRollingPolicy.setParent(rollingFileAppender);
			fixedWindowRollingPolicy.setMinIndex(1);
			fixedWindowRollingPolicy.setMaxIndex(config.getFileMaxIndex());
			String fileNamePattern = config.getFileNamePattern();
			if (config.isFileCompressionEnabled()) {
				fileNamePattern += Util.isWindows() ? ".zip" : ".gz";
			}
			fixedWindowRollingPolicy.setFileNamePattern(fileNamePattern);
			fixedWindowRollingPolicy.start();

			final SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<>();
			sizeBasedTriggeringPolicy.setContext(context);
			sizeBasedTriggeringPolicy.setMaxFileSize(new FileSize(config.getFileMaxSize()));
			rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
			rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);
			sizeBasedTriggeringPolicy.start();

			final ThresholdFilter fileThresholdFilter = new ThresholdFilter();
			fileThresholdFilter.setContext(context);
			fileThresholdFilter.setLevel(config.getFileLevel().toString());
			rollingFileAppender.addFilter(fileThresholdFilter);
			fileThresholdFilter.start();

			rollingFileAppender.start();
			rootLogger.addAppender(rollingFileAppender);
		}

		rootLogger.addAppender(consoleAppender);
		rootLogger.setLevel(config.getRootLevel());

		final LevelChangePropagator levelChangePropagator = new LevelChangePropagator(); // Propagate level changes made to a logback logger into the equivalent logger in JUL
		levelChangePropagator.setContext(context);
		context.addListener(levelChangePropagator);
		levelChangePropagator.start();

		addInfo("Logging configuration reloaded successfully.");
		return ExecutionStatus.NEUTRAL;
	}

}
