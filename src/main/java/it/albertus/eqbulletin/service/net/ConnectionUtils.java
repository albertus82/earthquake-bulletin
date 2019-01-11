package it.albertus.eqbulletin.service.net;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.util.StringUtils;
import it.albertus.util.logging.LoggerFactory;

public class ConnectionUtils {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);

	public static Charset detectCharset(final URLConnection connection) {
		String contentType = connection.getContentType();
		if (contentType != null) {
			contentType = contentType.toLowerCase();
			if (contentType.contains("charset=")) {
				final String charsetName = StringUtils.substringAfter(contentType, "charset=").trim();
				try {
					final Charset charset = Charset.forName(charsetName);
					logger.log(Level.FINE, "Charset detected: {0}", charset);
					return charset;
				}
				catch (final IllegalArgumentException e) {
					logger.log(Level.WARNING, "Cannot detect charset for name \"" + charsetName + "\":", e);
				}
			}
		}
		final Charset charset = StandardCharsets.ISO_8859_1;
		logger.log(Level.FINE, "Using default HTTP 1.1 charset: {0}", charset);
		return charset;
	}

	private ConnectionUtils() {
		throw new IllegalAccessError("Utility class");
	}

}
