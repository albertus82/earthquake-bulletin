package it.albertus.eqbulletin.config;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import it.albertus.util.logging.ILoggingManager;

public class LogbackLoggingManager implements ILoggingManager {

	@Override
	public void initializeLogging() {
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
