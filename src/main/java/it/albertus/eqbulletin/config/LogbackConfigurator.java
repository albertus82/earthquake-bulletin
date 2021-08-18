package it.albertus.eqbulletin.config;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.layout.TTLLLayout;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.spi.ContextAwareBase;

public class LogbackConfigurator extends ContextAwareBase implements Configurator {

	public void configure(LoggerContext lc) {
		addInfo("Setting up default configuration.");

		ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
		ca.setContext(lc);
		ca.setName("console");
		LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<ILoggingEvent>();
		encoder.setContext(lc);

		// same as 
		// PatternLayout layout = new PatternLayout();
		// layout.setPattern("%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n");
		TTLLLayout layout = new TTLLLayout();

		layout.setContext(lc);
		layout.start();
		encoder.setLayout(layout);

		ca.setEncoder(encoder);
		ca.start();

		Logger rootLogger = lc.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(ca);
		rootLogger.setLevel(Level.INFO);
	}
}
