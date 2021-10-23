package com.github.albertus82.eqbulletin.service.net;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConnectionUtilsTest {

	@Test
	void testDetectCharset() {
		Assertions.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset((String) null));
		Assertions.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset(""));
		Assertions.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json"));
		Assertions.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json; charset=QWERTYUIOP"));
		Assertions.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json; charset=iso-8859-1"));
		Assertions.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json; charset=ISO-8859-1"));
		Assertions.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json; charset=UTF-8"));
		Assertions.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json;charset=utf-8"));
		Assertions.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json;charset=utf8"));
		Assertions.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json;charset= UTF-8"));
		Assertions.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json; charset= UTF-8"));
		Assertions.assertEquals(StandardCharsets.US_ASCII, ConnectionUtils.detectCharset("application/json; charset=us-ascii"));
	}

	@Test
	void testSanitizeUriString() throws MalformedURLException {
		Assertions.assertEquals("https://www.example.com", ConnectionUtils.sanitizeUriString("https://www.example.com"));
		Assertions.assertEquals("https://www.example.com/", ConnectionUtils.sanitizeUriString(" https://www.example.com/"));
		Assertions.assertEquals("https://www.example.com", ConnectionUtils.sanitizeUriString("https://www.example.com "));
		Assertions.assertEquals("https://www.example.com/", ConnectionUtils.sanitizeUriString(" https://www.example.com/ "));
		Assertions.assertEquals("https://www.example.com", ConnectionUtils.sanitizeUriString("\t  \t https://www.example.com  "));
		Assertions.assertEquals("http://www.example.com", ConnectionUtils.sanitizeUriString("http://www.example.com"));
		Assertions.assertEquals("http://www.example.com/", ConnectionUtils.sanitizeUriString("  http://www.example.com/  "));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.sanitizeUriString("file:///etc/passwd"));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.sanitizeUriString("dict://dict.example.com"));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.sanitizeUriString("ftp://ftp.example.com"));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.sanitizeUriString("gopher://gph.example.com"));
	}

	@Test
	@SuppressWarnings("java:S5783")
	void testValidateUrl() {
		Assertions.assertDoesNotThrow(() -> ConnectionUtils.validateUrl(new URL("https://www.example.com")));
		Assertions.assertDoesNotThrow(() -> ConnectionUtils.validateUrl(new URL("http://www.example.com")));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.validateUrl(new URL("file:///etc/passwd")));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.validateUrl(new URL("dict://dict.example.com")));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.validateUrl(new URL("ftp://ftp.example.com")));
		Assertions.assertThrows(MalformedURLException.class, () -> ConnectionUtils.validateUrl(new URL("gopher://gph.example.com")));
	}

}
