package it.albertus.eqbulletin.config;

import static it.albertus.eqbulletin.EarthquakeBulletin.ARTIFACT_ID;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.util.Util;

import it.albertus.eqbulletin.resources.LanguageManager;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.util.InitializationException;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.jface.preference.PreferencesConfiguration;
import it.albertus.util.ILanguageManager;
import it.albertus.util.SystemUtils;
import it.albertus.util.config.Configuration;
import it.albertus.util.config.PropertiesConfiguration;
import it.albertus.util.logging.ILoggingManager;
import it.albertus.util.logging.LoggerFactory;
import it.albertus.util.logging.LoggingManager;

public class EarthquakeBulletinConfig extends Configuration {

	private static final Logger logger = LoggerFactory.getLogger(EarthquakeBulletinConfig.class);

	private static final String DIRECTORY_NAME = Util.isLinux() ? '.' + ARTIFACT_ID : "Earthquake Bulletin";

	public static final String APPDATA_DIRECTORY = SystemUtils.getOsSpecificLocalAppDataDir() + File.separator + DIRECTORY_NAME;

	private static final String CFG_FILE_NAME = Util.isLinux() ? ARTIFACT_ID + ".cfg" : "EarthquakeBulletin.cfg";

	private static EarthquakeBulletinConfig instance;
	private static IPreferencesConfiguration wrapper;
	private static int instanceCount = 0;

	private final ILoggingManager loggingManager;
	private final ILanguageManager languageManager;

	private EarthquakeBulletinConfig(final boolean initialize) throws IOException {
		super(new PropertiesConfiguration(DIRECTORY_NAME + File.separator + CFG_FILE_NAME, true));
		final IPreferencesConfiguration pc = new PreferencesConfiguration(this);
		loggingManager = new LoggingManager(new LoggingConfig(pc), initialize);
		languageManager = new LanguageManager(new LanguageConfig(pc), initialize);
	}

	private static synchronized EarthquakeBulletinConfig getInstance() {
		if (instance == null) {
			try {
				instance = new EarthquakeBulletinConfig(false);
				instanceCount++;
				if (logger.isLoggable(Level.CONFIG)) {
					logger.log(Level.CONFIG, "Created {0} instance.", instance.getClass().getSimpleName());
				}
				if (instanceCount > 1) {
					throw new InitializationException("Detected multiple instances of singleton " + instance.getClass());
				}
			}
			catch (final IOException e) {
				throw new InitializationException(Messages.get("err.open.cfg", CFG_FILE_NAME), e);
			}
		}
		return instance;
	}

	public static synchronized IPreferencesConfiguration getPreferencesConfiguration() {
		if (wrapper == null) {
			wrapper = new PreferencesConfiguration(getInstance());
		}
		return wrapper;
	}

	public static void initialize() {
		final EarthquakeBulletinConfig instance = getInstance();
		instance.loggingManager.initializeLogging();
		instance.languageManager.resetLanguage();
	}

	@Override
	public void reload() throws IOException {
		super.reload();
		initialize();
	}

}
