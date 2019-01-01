package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MapImage;
import it.albertus.util.IOUtils;

public class MapImageDownloader implements Downloader<Earthquake, MapImage> {

	private static final short BUFFER_SIZE = 8192;

	@Override
	public MapImage download(final Earthquake earthquake) throws IOException {
		return download(earthquake, null);
	}

	@Override
	public MapImage download(final Earthquake earthquake, final MapImage cached) throws IOException {
		final Headers headers = new Headers();
		headers.set("Accept", "image/*,*/*;0.9");
		headers.set("Accept-Encoding", "gzip");
		if (cached != null && cached.getEtag() != null && !cached.getEtag().trim().isEmpty()) {
			headers.set("If-None-Match", cached.getEtag());
		}
		final HttpURLConnection connection = ConnectionFactory.makeGetRequest(earthquake.getEnclosureUrl(), headers);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
			return cached; // Not modified.
		}
		else {
			final String responseContentEncoding = connection.getContentEncoding();
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw; final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				IOUtils.copy(in, out, BUFFER_SIZE);
				return new MapImage(out.toByteArray(), connection.getHeaderField("Etag"));
			}
		}
	}

}
