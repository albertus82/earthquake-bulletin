package it.albertus.eqbulletin.config;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.eclipse.jface.util.Util;

import it.albertus.eqbulletin.config.logging.LoggingConfigAccessor;
import it.albertus.eqbulletin.config.logging.LoggingManager;
import it.albertus.eqbulletin.resources.LanguageManager;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.util.BuildInfo;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.jface.preference.PreferencesConfiguration;
import it.albertus.util.ILanguageManager;
import it.albertus.util.InitializationException;
import it.albertus.util.SystemUtils;
import it.albertus.util.config.Configuration;
import it.albertus.util.config.PropertiesConfiguration;
import it.albertus.util.logging.ILoggingManager;

public class EarthquakeBulletinConfig extends Configuration {

	private static final String DIRECTORY_NAME = Util.isLinux() ? '.' + BuildInfo.getProperty("project.artifactId") : BuildInfo.getProperty("project.name");

	public static final String APPDATA_DIRECTORY = SystemUtils.getOsSpecificLocalAppDataDir() + File.separator + DIRECTORY_NAME;

	private static final String CFG_FILE_NAME = (Util.isLinux() ? BuildInfo.getProperty("project.artifactId") : BuildInfo.getProperty("project.name").replace(" ", "")) + ".cfg";

	private static volatile EarthquakeBulletinConfig instance; // NOSONAR Use a thread-safe type; adding "volatile" is not enough to make this field thread-safe. Use a thread-safe type; adding "volatile" is not enough to make this field thread-safe.
	private static volatile IPreferencesConfiguration wrapper; // NOSONAR Use a thread-safe type; adding "volatile" is not enough to make this field thread-safe. Use a thread-safe type; adding "volatile" is not enough to make this field thread-safe.
	private static int instanceCount = 0;

	private final ILoggingManager loggingManager;
	private final ILanguageManager languageManager;

	private EarthquakeBulletinConfig() throws IOException {
		super(new PropertiesConfiguration(DIRECTORY_NAME + File.separator + CFG_FILE_NAME, true));
		final IPreferencesConfiguration pc = new PreferencesConfiguration(this);
		loggingManager = new LoggingManager(new LoggingConfigAccessor(pc));
		languageManager = new LanguageManager(new LanguageConfigAccessor(pc));
	}

	private static EarthquakeBulletinConfig getInstance() {
		if (instance == null) {
			synchronized (EarthquakeBulletinConfig.class) {
				if (instance == null) { // The field needs to be volatile to prevent cache incoherence issues
					try {
						instance = new EarthquakeBulletinConfig();
						if (++instanceCount > 1) {
							throw new IllegalStateException("Detected multiple instances of singleton " + instance.getClass());
						}
					}
					catch (final IOException e) {
						throw new UncheckedIOException(Messages.get("error.open.cfg", CFG_FILE_NAME), e);
					}
				}
			}
		}
		return instance;
	}

	public static IPreferencesConfiguration getPreferencesConfiguration() {
		if (wrapper == null) {
			synchronized (EarthquakeBulletinConfig.class) {
				if (wrapper == null) { // The field needs to be volatile to prevent cache incoherence issues
					wrapper = new PreferencesConfiguration(getInstance());
				}
			}
		}
		return wrapper;
	}

	public static void initialize() {
		try {
			final EarthquakeBulletinConfig config = getInstance();
			config.loggingManager.initializeLogging();
			config.languageManager.resetLanguage();
		}
		catch (final RuntimeException e) {
			throw new InitializationException(e);
		}
	}

	@Override
	public void reload() throws IOException {
		super.reload();
		initialize();
	}

}
