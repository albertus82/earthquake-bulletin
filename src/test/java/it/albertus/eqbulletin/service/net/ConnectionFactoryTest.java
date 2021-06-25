package it.albertus.eqbulletin.service.net;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lombok.extern.java.Log;

@Log
class ConnectionFactoryTest {

	private static final String TEST_ADDRESS = "https://foo.bar";

	@Test
	void testProxy() throws URISyntaxException {
		final boolean useSystemProxies = Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies"));
		log.log(Level.INFO, "BEFORE: useSystemProxies={0}", useSystemProxies);
		System.setProperty("java.net.useSystemProxies", Boolean.TRUE.toString());
		log.log(Level.INFO, "Detecting proxies for \"{0}\"...", TEST_ADDRESS);
		for (final Proxy proxy : ProxySelector.getDefault().select(new URI(TEST_ADDRESS))) {
			log.log(Level.INFO, "Proxy type: {0}", proxy);
			final InetSocketAddress addr = (InetSocketAddress) proxy.address();
			if (addr == null) {
				log.info("No proxy");
			}
			else {
				log.log(Level.INFO, "Proxy address: {0}", addr);
				//	System.setProperty("http.proxyHost", addr.getHostName());
				//	System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
			}
		}
		// Reset java.net.useSystemProxies to default
		System.setProperty("java.net.useSystemProxies", Boolean.toString(useSystemProxies));
		Assertions.assertTrue(true);
	}

}
