package it.albertus.eqbulletin.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.eqbulletin.gui.preference.Preference;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.util.InitializationException;
import it.albertus.jface.preference.PreferencesConfiguration;
import it.albertus.util.NewLine;
import it.albertus.util.SystemUtils;
import it.albertus.util.config.LanguageConfig;
import it.albertus.util.config.LoggingConfig;
import it.albertus.util.logging.LoggerFactory;

public class EarthquakeBulletinConfig extends LoggingConfig implements LanguageConfig {

	private static final Logger logger = LoggerFactory.getLogger(EarthquakeBulletinConfig.class);

	public static final String DEFAULT_LOGGING_FILES_PATH = SystemUtils.getOsSpecificLocalAppDataDir() + File.separator + Messages.get("msg.application.name");
	public static final Level DEFAULT_LOGGING_LEVEL = Level.WARNING;

	public static final String CFG_FILE_NAME = "earthquake-bulletin.cfg";
	public static final String LOG_FILE_NAME_PATTERN = "earthquake-bulletin.%g.log";

	private static PreferencesConfiguration instance;
	private static volatile int instanceCount = 0;

	private EarthquakeBulletinConfig() throws IOException {
		super(Messages.get("msg.application.name") + File.separator + CFG_FILE_NAME, true);
		init();
	}

	public static synchronized PreferencesConfiguration getInstance() {
		if (instance == null) {
			try {
				instance = new PreferencesConfiguration(new EarthquakeBulletinConfig());
				if (logger.isLoggable(Level.CONFIG)) {
					logger.log(Level.CONFIG, "Created {0} instance {1}", new String[] { PreferencesConfiguration.class.getSimpleName(), Arrays.toString(Thread.currentThread().getStackTrace()).replace(", ", NewLine.SYSTEM_LINE_SEPARATOR + '\t') });
				}
				if (++instanceCount > 1) {
					throw new IllegalStateException("Detected multiple instances of singleton " + PreferencesConfiguration.class);
				}
			}
			catch (final IOException e) {
				final String message = Messages.get("err.open.cfg", CFG_FILE_NAME);
				logger.log(Level.SEVERE, message, e);
				throw new InitializationException(message, e);
			}
		}
		return instance;
	}

	@Override
	protected void init() {
		super.init();
		updateLanguage();
	}

	@Override
	public void updateLanguage() {
		Messages.setLanguage(getString(Preference.LANGUAGE.getName(), Messages.DEFAULT_LANGUAGE));
	}

	@Override
	protected boolean isFileHandlerEnabled() {
		return getBoolean("logging.files.enabled", super.isFileHandlerEnabled());
	}

	@Override
	protected String getLoggingLevel() {
		return getString("logging.level", DEFAULT_LOGGING_LEVEL.getName());
	}

	@Override
	protected String getFileHandlerPattern() {
		return getString("logging.files.path", DEFAULT_LOGGING_FILES_PATH) + File.separator + LOG_FILE_NAME_PATTERN;
	}

	@Override
	protected int getFileHandlerLimit() {
		final Integer limit = getInt("logging.files.limit");
		if (limit != null) {
			return limit * 1024;
		}
		else {
			return super.getFileHandlerLimit();
		}
	}

	@Override
	protected int getFileHandlerCount() {
		return getInt("logging.files.count", super.getFileHandlerCount());
	}

	@Override
	protected String getFileHandlerFormat() {
		return EarthquakeBulletin.LOG_FORMAT;
	}

}
