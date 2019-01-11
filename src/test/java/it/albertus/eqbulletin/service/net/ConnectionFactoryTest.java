package it.albertus.eqbulletin.service.net;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import it.albertus.util.logging.LoggerFactory;

public class ConnectionFactoryTest {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionFactoryTest.class);

	private static final String TEST_ADDRESS = "https://foo.bar";

	@Test
	public void testProxy() throws URISyntaxException {
		final boolean useSystemProxies = Boolean.parseBoolean(System.getProperty("java.net.useSystemProxies"));
		logger.log(Level.INFO, "BEFORE: useSystemProxies={0}", useSystemProxies);
		System.setProperty("java.net.useSystemProxies", Boolean.TRUE.toString());
		logger.log(Level.INFO, "Detecting proxies for \"{0}\"...", TEST_ADDRESS);
		for (final Proxy proxy : ProxySelector.getDefault().select(new URI(TEST_ADDRESS))) {
			logger.log(Level.INFO, "Proxy type: {0}", proxy);
			final InetSocketAddress addr = (InetSocketAddress) proxy.address();
			if (addr == null) {
				logger.info("No proxy");
			}
			else {
				logger.log(Level.INFO, "Proxy address: {0}", addr);
//				System.setProperty("http.proxyHost", addr.getHostName());
//				System.setProperty("http.proxyPort", Integer.toString(addr.getPort()));
			}
		}
		// Reset java.net.useSystemProxies to default
		System.setProperty("java.net.useSystemProxies", Boolean.toString(useSystemProxies));
		Assert.assertTrue(true);
	}

}
