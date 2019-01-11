package it.albertus.eqbulletin.service.net;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;

public class ConnectionUtilsTest {

	@Test
	public void testDetectCharset() {
		Assert.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset((String) null));
		Assert.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset(""));
		Assert.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json"));
		Assert.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json; charset=QWERTYUIOP"));
		Assert.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json; charset=iso-8859-1"));
		Assert.assertEquals(StandardCharsets.ISO_8859_1, ConnectionUtils.detectCharset("application/json; charset=ISO-8859-1"));
		Assert.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json; charset=UTF-8"));
		Assert.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json;charset=utf-8"));
		Assert.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json;charset=utf8"));
		Assert.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json;charset= UTF-8"));
		Assert.assertEquals(StandardCharsets.UTF_8, ConnectionUtils.detectCharset("application/json; charset= UTF-8"));
		Assert.assertEquals(StandardCharsets.US_ASCII, ConnectionUtils.detectCharset("application/json; charset=us-ascii"));
	}

}
