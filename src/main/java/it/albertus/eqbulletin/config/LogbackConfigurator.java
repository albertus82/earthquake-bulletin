package it.albertus.eqbulletin.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
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

	private final LoggingConfig config = new LoggingConfig(EarthquakeBulletinConfig.getPreferencesConfiguration());

	public void configure(final LoggerContext context) {
		final ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
		consoleAppender.setContext(context);
		consoleAppender.setName("consoleAppender");
		final LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
		encoder.setContext(context);

		final PatternLayout layout = new PatternLayout();
		layout.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n");

		layout.setContext(context);
		layout.start();
		encoder.setLayout(layout);

		consoleAppender.setEncoder(encoder);
		consoleAppender.start();

		RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
		if (config.isFileHandlerEnabled()) {
			rollingFileAppender.setContext(context);
			rollingFileAppender.setName("rollingFileAppender");
			rollingFileAppender.setEncoder(encoder);
			rollingFileAppender.setAppend(true);
			rollingFileAppender.setFile(config.getFileHandlerPattern().replace("%i", "0"));

			FixedWindowRollingPolicy fixedWindowRollingPolicy = new FixedWindowRollingPolicy();
			fixedWindowRollingPolicy.setContext(context);
			fixedWindowRollingPolicy.setParent(rollingFileAppender);
			fixedWindowRollingPolicy.setMinIndex(1);
			fixedWindowRollingPolicy.setMaxIndex(config.getFileHandlerCount());
			fixedWindowRollingPolicy.setFileNamePattern(config.getFileHandlerPattern());
			fixedWindowRollingPolicy.start();

			SizeBasedTriggeringPolicy<ILoggingEvent> sizeBasedTriggeringPolicy = new SizeBasedTriggeringPolicy<>();
			sizeBasedTriggeringPolicy.setContext(context);
			sizeBasedTriggeringPolicy.setMaxFileSize(new FileSize(config.getFileHandlerLimit()));
			rollingFileAppender.setTriggeringPolicy(sizeBasedTriggeringPolicy);
			rollingFileAppender.setRollingPolicy(fixedWindowRollingPolicy);
			sizeBasedTriggeringPolicy.start();
			rollingFileAppender.start();
		}

		final Logger rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(consoleAppender);
		if (config.isFileHandlerEnabled()) {
			rootLogger.addAppender(rollingFileAppender);
		}
		rootLogger.setLevel(Level.toLevel(config.getLoggingLevel()));
	}

}
