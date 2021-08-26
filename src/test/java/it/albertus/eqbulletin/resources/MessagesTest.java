package it.albertus.eqbulletin.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import it.albertus.eqbulletin.BaseTest;
import it.albertus.eqbulletin.EarthquakeBulletin;
import it.albertus.util.StringUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class MessagesTest extends BaseTest {

	@Test
	void checkMessageFiles() throws IOException {
		checkMessageFiles(getResourceNames(Messages.class), null);
	}

	private void checkMessageFiles(@NonNull final Iterable<String> resourceNames, final String prefix) throws IOException {
		final Collection<Properties> pp = new ArrayList<>();
		for (final String resourceName : resourceNames) {
			final Properties p = new Properties();
			pp.add(p);
			try (final InputStream is = getClass().getResourceAsStream('/' + resourceName)) {
				Assertions.assertNotNull(is, "Missing resource file: " + resourceName);
				p.load(is);
			}
			log.info("{} messages found in: {}", p.size(), resourceName);
			Assertions.assertFalse(p.isEmpty(), "Empty resource file: " + resourceName);
		}
		pp.stream().reduce((p1, p2) -> {
			if (prefix != null) {
				p1.keySet().forEach(e -> Assertions.assertTrue(e.toString().startsWith(prefix), "Invalid property key '" + e + "': expected prefix '" + prefix + "'!"));
				p2.keySet().forEach(e -> Assertions.assertTrue(e.toString().startsWith(prefix), "Invalid property key '" + e + "': expected prefix '" + prefix + "'!"));
			}
			Assertions.assertTrue(p1.keySet().containsAll(p2.keySet()), "Uneven resource files: " + resourceNames);
			Assertions.assertTrue(p2.keySet().containsAll(p1.keySet()), "Uneven resource files: " + resourceNames);
			return p1;
		});
	}

	@Test
	void checkMissingMessages() throws IOException {
		final Set<String> keys = new TreeSet<>();
		try (final Stream<Path> paths = newSourceStream()) {
			paths.forEach(path -> {
				log.debug("{}", path);
				try {
					// @formatter:off
					keys.addAll(Files.readAllLines(path).stream()
							.map(line -> line.trim().replace(" ", ""))
							.filter(e -> e.toLowerCase(Locale.ROOT).contains("messages.get(\""))
							.flatMap(e -> Arrays.stream(e.split("(?i)(?>=messages\\.get\\(\")|(?=messages\\.get\\(\")")))
							.filter(e -> e.toLowerCase(Locale.ROOT).startsWith("messages"))
							.map(e -> StringUtils.substringBefore(StringUtils.substringAfter(e, "\""), "\""))
							.filter(key -> !key.endsWith("."))
							.collect(Collectors.toSet()));
					// @formatter:on
				}
				catch (final IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
		log.info("Found {} message keys referenced in sources", keys.size());
		Assertions.assertFalse(keys.isEmpty(), "No message keys found in sources");
		final Collection<String> allKeys = Messages.getKeys();
		log.info("{} message keys available in resource bundle: {}", allKeys.size(), Messages.class.getSimpleName());
		Assertions.assertFalse(allKeys.isEmpty(), "No message keys found in resource bundle: " + Messages.class.getSimpleName());
		for (final String key : new TreeSet<>(keys)) {
			Assertions.assertTrue(allKeys.contains(key), "Missing message key '" + key + "'");
		}
	}

	@Test
	void checkUnreferencedMessages() throws IOException {
		checkUnreferencedMessages(getResourceNames(Messages.class).iterator().next());
	}

	@Test
	void testFallback() {
		final String validKey = "lbl.system.info.dialog.title";
		Assertions.assertNotEquals(validKey, Messages.get(validKey));
		final String nonExistentKey = "qwertyuiop.asdfghjkl.zxcvbnm";
		Assertions.assertEquals(nonExistentKey, Messages.get(nonExistentKey));
	}

	private void checkUnreferencedMessages(@NonNull final String resourceName) throws IOException {
		final Properties p = new Properties();
		try (final InputStream is = getClass().getResourceAsStream('/' + resourceName)) {
			Assertions.assertNotNull(is, "Missing resource file: " + resourceName);
			p.load(is);
		}
		log.info("{} messages found in: {}", p.size(), resourceName);
		Assertions.assertFalse(p.isEmpty(), "Empty resource file: " + resourceName);
		final Set<String> usedKeys = new TreeSet<>();
		final Set<String> allKeys = new TreeSet<>(Collections.list(p.propertyNames()).stream().map(Object::toString).collect(Collectors.toSet()));
		try (final Stream<Path> paths = newSourceStream()) {
			for (final Path path : paths.collect(Collectors.toSet())) {
				try (final BufferedReader br = Files.newBufferedReader(path)) {
					String line;
					while ((line = br.readLine()) != null) {
						final String clean = line.trim().replace(" ", "").replace("\"", "");
						for (final String key : allKeys) {
							if (clean.contains(key)) {
								usedKeys.add(key);
							}
						}
					}
				}
			}
		}

		log.info("Found {} message keys referenced in sources", usedKeys.size());

		final Set<String> unreferencedKeys = new TreeSet<>(allKeys.stream().filter(key -> !usedKeys.contains(key)).collect(Collectors.toSet()));
		if (!unreferencedKeys.isEmpty()) {
			log.warn("Unreferenced message keys: {}", unreferencedKeys);
		}
	}

	private static Stream<Path> newSourceStream() throws IOException {
		final Path sourcesPath = Paths.get(projectProperties.getProperty("project.build.sourceDirectory"), EarthquakeBulletin.class.getPackage().getName().replace('.', File.separatorChar));
		log.info("Sources path: {}", sourcesPath);
		return Files.walk(sourcesPath).filter(Files::isRegularFile).filter(p -> p.toString().toLowerCase(Locale.ROOT).endsWith(".java"));
	}

	private static Set<String> getResourceNames(final Class<?> messagesClass) {
		final Reflections reflections = new Reflections(messagesClass.getPackage().getName(), new ResourcesScanner());
		final Set<String> resourceNames = reflections.getResources(name -> name.contains(messagesClass.getSimpleName().toLowerCase(Locale.ROOT)) && name.endsWith(".properties"));
		log.info("Resources found: {}", resourceNames);
		return resourceNames;
	}

}
