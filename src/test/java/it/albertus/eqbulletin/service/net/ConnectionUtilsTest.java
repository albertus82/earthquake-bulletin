package it.albertus.eqbulletin.service.net;

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

}
