package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.util.IOUtils;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorDownloader {

	private static final short BUFFER_SIZE = 512;

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorDownloader.class);

	public static MomentTensor download(final Earthquake earthquake) throws IOException {
		return download(earthquake, null);
	}

	public static MomentTensor download(final Earthquake earthquake, final MomentTensor cached) throws IOException {
		final Headers headers = new Headers();
		headers.set("Accept", "text/plain,text/*;q=0.9,*/*;q=0.8");
		headers.set("Accept-Encoding", "gzip");
		if (cached != null && cached.getEtag() != null && !cached.getEtag().trim().isEmpty()) {
			headers.set("If-None-Match", cached.getEtag());
		}
		final HttpURLConnection connection = ConnectionFactory.makeGetRequest(earthquake.getMomentTensorUrl(), headers);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
			return cached; // Not modified.
		}
		else {
			final String responseContentEncoding = connection.getContentEncoding();
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			final Charset charset = ConnectionUtils.detectCharset(connection);
			try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw; final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				IOUtils.copy(in, out, BUFFER_SIZE);
				final MomentTensor downloaded = new MomentTensor(out.toString(charset.name()), connection.getHeaderField("Etag"));
				if (downloaded.equals(cached)) {
					logger.fine("downloaded.equals(cached)");
					return cached;
				}
				else {
					return downloaded;
				}
			}
		}
	}

	private MomentTensorDownloader() {
		throw new IllegalAccessError();
	}

}
