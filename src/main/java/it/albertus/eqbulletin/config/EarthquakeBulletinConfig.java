package it.albertus.eqbulletin.config;

import java.io.File;
import java.io.IOException;

import org.eclipse.jface.util.Util;

import it.albertus.eqbulletin.resources.LanguageManager;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.util.BuildInfo;
import it.albertus.eqbulletin.util.InitializationException;
import it.albertus.jface.preference.IPreferencesConfiguration;
import it.albertus.jface.preference.PreferencesConfiguration;
import it.albertus.util.ILanguageManager;
import it.albertus.util.SystemUtils;
import it.albertus.util.config.Configuration;
import it.albertus.util.config.PropertiesConfiguration;
import it.albertus.util.logging.ILoggingManager;

public class EarthquakeBulletinConfig extends Configuration {

	private static final String DIRECTORY_NAME = Util.isLinux() ? '.' + BuildInfo.getProperty("project.artifactId") : BuildInfo.getProperty("project.name");

	public static final String APPDATA_DIRECTORY = SystemUtils.getOsSpecificLocalAppDataDir() + File.separator + DIRECTORY_NAME;

	private static final String CFG_FILE_NAME = (Util.isLinux() ? BuildInfo.getProperty("project.artifactId") : BuildInfo.getProperty("project.name").replace(" ", "")) + ".cfg";

	private static EarthquakeBulletinConfig instance;
	private static IPreferencesConfiguration wrapper;
	private static int instanceCount = 0;

	private final ILoggingManager loggingManager;
	private final ILanguageManager languageManager;

	private EarthquakeBulletinConfig() throws IOException {
		super(new PropertiesConfiguration(DIRECTORY_NAME + File.separator + CFG_FILE_NAME, true));
		final IPreferencesConfiguration pc = new PreferencesConfiguration(this);
		loggingManager = new LoggingManager(new LoggingConfigAccessor(pc));
		languageManager = new LanguageManager(new LanguageConfigAccessor(pc));
	}

	private static synchronized EarthquakeBulletinConfig getInstance() {
		if (instance == null) {
			try {
				instance = new EarthquakeBulletinConfig();
				instanceCount++;
				if (instanceCount > 1) {
					throw new InitializationException("Detected multiple instances of singleton " + instance.getClass());
				}
			}
			catch (final IOException e) {
				throw new InitializationException(Messages.get("error.open.cfg", CFG_FILE_NAME), e);
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
