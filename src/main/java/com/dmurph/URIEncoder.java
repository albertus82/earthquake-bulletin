package com.dmurph;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Simple uri encoder, made from the spec at http://www.ietf.org/rfc/rfc2396.txt
 * Feel free to copy this. I'm not responsible for this code in any way, ever.
 * Thanks to Marco and Thomas
 * 
 * @author Daniel Murphy
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class URIEncoder {

	private static final String MARK = "-_.!~*'()\"";
	private static final char[] hex = "0123456789ABCDEF".toCharArray();

	public static String encodeURI(@NonNull final CharSequence argString) {
		final StringBuilder uri = new StringBuilder();

		for (int i = 0; i < argString.length(); i++) {
			final char c = argString.charAt(i);
			if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || MARK.indexOf(c) != -1) {
				uri.append(c);
			}
			else {
				appendEscaped(uri, c);
			}
		}
		return uri.toString();
	}

	private static void appendEscaped(final StringBuilder uri, final char c) {
		if (c <= (char) 0xF) {
			uri.append("%0");
			uri.append(hex[c]);
		}
		else if (c <= (char) 0xFF) {
			uri.append('%');
			uri.append(hex[c >> 8]);
			uri.append(hex[c & 0xF]);
		}
		else {
			// unicode
			uri.append("\\u");
			uri.append(hex[c >> 24]);
			uri.append(hex[(c >> 16) & 0xF]);
			uri.append(hex[(c >> 8) & 0xF]);
			uri.append(hex[c & 0xF]);
		}
	}

}
