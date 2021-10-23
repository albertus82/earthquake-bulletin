package io.github.albertus82.eqbulletin.service.net;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConnectionFactoryTest {

	private static final String TEST_ADDRESS = "https://foo.bar";

	@Test
	void testProxy() throws URISyntaxException {
		final boolean useSystemProxies = Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies"));
		log.info("BEFORE: useSystemProxies={}", useSystemProxies);
		System.setProperty("java.net.useSystemProxies", Boolean.TRUE.toString());
		log.info("Detecting proxies for \"{}\"...", TEST_ADDRESS);
		for (final Proxy proxy : ProxySelector.getDefault().select(new URI(TEST_ADDRESS))) {
			log.info("Proxy type: {}", proxy);
			final InetSocketAddress addr = (InetSocketAddress) proxy.address();
			if (addr == null) {
				log.info("No proxy");
			}
			else {
				log.info("Proxy address: {}", addr);
				//	System.setProperty("http.proxyHost", addr.getHostName());
				//	System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
			}
		}
		// Reset java.net.useSystemProxies to default
		System.setProperty("java.net.useSystemProxies", Boolean.toString(useSystemProxies));
		Assertions.assertTrue(true);
	}

}
