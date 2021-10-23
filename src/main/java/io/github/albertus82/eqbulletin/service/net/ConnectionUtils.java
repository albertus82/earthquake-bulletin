package io.github.albertus82.eqbulletin.service.net;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import io.github.albertus82.util.StringUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConnectionUtils {

	public static String sanitizeUriString(@NonNull final String spec) throws MalformedURLException {
		final String sanitized = spec.trim();
		final String lower = sanitized.toLowerCase(Locale.ROOT);
		if (!lower.startsWith("http:") && !lower.startsWith("https:")) {
			throw new MalformedURLException("Illegal or missing protocol (only http and https are allowed)");
		}
		return sanitized;
	}

	public static URI toURI(@NonNull final String spec) throws MalformedURLException, URISyntaxException {
		return new URI(sanitizeUriString(spec));
	}

	static Charset detectCharset(@NonNull final URLConnection connection) {
		return detectCharset(connection.getContentType());
	}

	static Charset detectCharset(String contentType) { // non-private for test only access
		if (contentType != null) {
			contentType = contentType.toLowerCase(Locale.ROOT);
			if (contentType.contains("charset=")) {
				final String charsetName = StringUtils.substringAfter(contentType, "charset=").trim();
				try {
					final Charset charset = Charset.forName(charsetName);
					log.debug("Charset detected: {}", charset);
					return charset;
				}
				catch (final IllegalArgumentException e) {
					log.warn("Cannot detect charset for name \"" + charsetName + "\":", e);
				}
			}
		}
		final Charset charset = StandardCharsets.ISO_8859_1;
		log.debug("Using default HTTP 1.1 charset: {}", charset);
		return charset;
	}

	static void validateUrl(@NonNull final URL url) throws MalformedURLException {
		if (!url.getProtocol().equalsIgnoreCase("http") && !url.getProtocol().equalsIgnoreCase("https")) {
			throw new MalformedURLException("Illegal or missing protocol (only http and https are allowed)");
		}
		log.debug("URL [{}] validated successfully.", url);
	}

}
