package it.albertus.eqbulletin.config;

import org.eclipse.jface.util.Util;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
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
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LogbackConfigurator extends ContextAwareBase implements Configurator {

	static {
		SLF4JBridgeHandler.removeHandlersForRootLogger(); // Remove existing handlers attached to JUL root logger
		SLF4JBridgeHandler.install(); // Add SLF4JBridgeHandler to JUL's root logger
	}

	private final LoggingConfigAccessor config = new LoggingConfigAccessor(EarthquakeBulletinConfig.getPreferencesConfiguration());

	public void configure(final LoggerContext context) {
		addInfo("Reloading logging configuration...");

		final ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
		consoleAppender.setContext(context);
		consoleAppender.setName("consoleAppender");
		final LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
		encoder.setContext(context);

		final PatternLayout layout = new PatternLayout();
		layout.setPattern(config.getFileHandlerFormat());
		layout.setContext(context);
		layout.start();
		encoder.setLayout(layout);

		consoleAppender.setEncoder(encoder);
		consoleAppender.start();

		final Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME); // NOSONAR Use static access with "org.slf4j.Logger" for "ROOT_LOGGER_NAME". "static" base class members should not be accessed via derived types (java:S3252)

		if (config.isFileHandlerEnabled()) {
			final RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
			rollingFileAppender.setContext(context);
			rollingFileAppender.setName("rollingFileAppender");
			rollingFileAppender.setEncoder(encoder);
			rollingFileAppender.setAppend(true);
			rollingFileAppender.setFile(config.getFileHandlerPattern().replace("%i", "0")); // The current file is the one with the zero index

			final FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
			fixedWindowRollingPolicy.setContext(context);
			fixedWindowRollingPolicy.setParent(rollingFileAppender);
			fixedWindowRollingPolicy.setMinIndex(1);
			fixedWindowRollingPolicy.setMaxIndex(config.getFileHandlerCount());
			String fileNamePattern = config.getFileHandlerPattern();
			if (config.isFileCompressionEnabled()) {
				fileNamePattern += Util.isWindows() ? ".zip" : ".gz";
			}
			fixedWindowRollingPolicy.setFileNamePattern(fileNamePattern);
			fixedWindowRollingPolicy.start();

			final SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<>();
			sizeBasedTriggeringPolicy.setContext(context);
			sizeBasedTriggeringPolicy.setMaxFileSize(new FileSize(config.getFileHandlerLimit()));
			rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
			rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);

			sizeBasedTriggeringPolicy.start();
			rollingFileAppender.start();
			rootLogger.addAppender(rollingFileAppender);
		}

		rootLogger.addAppender(consoleAppender);
		rootLogger.setLevel(Level.toLevel(config.getLoggingLevel()));

		final LevelChangePropagator levelChangePropagator = new LevelChangePropagator(); // Propagate level changes made to a logback logger into the equivalent logger in JUL
		levelChangePropagator.setContext(context);
		context.addListener(levelChangePropagator);
		levelChangePropagator.start();

		addInfo("Logging configuration reloaded successfully.");
	}

}
