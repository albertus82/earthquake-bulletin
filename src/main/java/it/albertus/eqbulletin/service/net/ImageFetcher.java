package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.MapImage;
import it.albertus.util.IOUtils;

public class ImageFetcher {

	private static final short BUFFER_SIZE = 8192;

	private ImageFetcher() {
		throw new IllegalAccessError();
	}

	public static MapImage downloadImage(final URL url, final String etag) throws IOException {
		final Headers headers = new Headers();
		headers.set("Accept", "image/*,*/*;0.9");
		headers.set("Accept-Encoding", "gzip");
		if (etag != null && !etag.trim().isEmpty()) {
			headers.set("If-None-Match", etag);
		}
		final HttpURLConnection urlConnection = ConnectionFactory.makeGetRequest(url, headers);
		try {
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
				return null;
			}
			else {
				final String responseContentEncoding = urlConnection.getContentEncoding();
				final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
				try (final InputStream internalInputStream = urlConnection.getInputStream(); final InputStream inputStream = gzip ? new GZIPInputStream(internalInputStream) : internalInputStream; final ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
					IOUtils.copy(inputStream, buffer, BUFFER_SIZE);
					return new MapImage(buffer.toByteArray(), urlConnection.getHeaderField("Etag"));
				}
			}
		}
		finally {
			urlConnection.disconnect();
		}
	}

}
