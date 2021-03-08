package it.albertus.eqbulletin.service.net;

import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Level;

import it.albertus.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

@Log
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionUtils {

	public static Charset detectCharset(final URLConnection connection) {
		return detectCharset(connection.getContentType());
	}

	public static Charset detectCharset(String contentType) {
		if (contentType != null) {
			contentType = contentType.toLowerCase(Locale.ROOT);
			if (contentType.contains("charset=")) {
				final String charsetName = StringUtils.substringAfter(contentType, "charset=").trim();
				try {
					final Charset charset = Charset.forName(charsetName);
					log.log(Level.FINE, "Charset detected: {0}", charset);
					return charset;
				}
				catch (final IllegalArgumentException e) {
					log.log(Level.WARNING, e, () -> "Cannot detect charset for name \"" + charsetName + "\":");
				}
			}
		}
		final Charset charset = StandardCharsets.ISO_8859_1;
		log.log(Level.FINE, "Using default HTTP 1.1 charset: {0}", charset);
		return charset;
	}

}
