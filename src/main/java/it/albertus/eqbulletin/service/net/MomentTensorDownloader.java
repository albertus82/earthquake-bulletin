package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import com.sun.net.httpserver.Headers;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensor;
import it.albertus.util.IOUtils;
import it.albertus.util.StringUtils;
import it.albertus.util.logging.LoggerFactory;

public class MomentTensorDownloader implements Downloader<Earthquake, MomentTensor> {

	private static final int BUFFER_SIZE = 512;

	private static final Logger logger = LoggerFactory.getLogger(MomentTensorDownloader.class);

	@Override
	public MomentTensor download(final Earthquake earthquake) throws IOException {
		return download(earthquake, null);
	}

	public MomentTensor download(final Earthquake earthquake, final MomentTensor cachedMomentTensor) throws IOException {
		///////////////////////////////////////////////////////////////////
		//				try {
		//					TimeUnit.SECONDS.sleep(10);
		//				}
		//				catch (InterruptedException e) {
		//					e.printStackTrace();
		//				}
		//				if (true) {
		//					throw new IOException("dsfdfds");
		//				}
		///////////////////////////////////////////////////////////////////
		final Headers headers = new Headers();
		headers.set("Accept", "text/*");
		headers.set("Accept-Encoding", "gzip");
		if (cachedMomentTensor != null && cachedMomentTensor.getEtag() != null && !cachedMomentTensor.getEtag().trim().isEmpty()) {
			headers.set("If-None-Match", cachedMomentTensor.getEtag());
		}
		final HttpURLConnection connection = ConnectionFactory.makeGetRequest(earthquake.getMomentTensorUrl(), headers);
		if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
			return cachedMomentTensor; // Not modified.
		}
		else {
			final String responseContentEncoding = connection.getContentEncoding();
			final boolean gzip = responseContentEncoding != null && responseContentEncoding.toLowerCase().contains("gzip");
			try (final InputStream raw = connection.getInputStream(); final InputStream in = gzip ? new GZIPInputStream(raw) : raw; final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
				IOUtils.copy(in, out, BUFFER_SIZE);
				String charsetName = StandardCharsets.US_ASCII.name();
				String contentType = connection.getContentType();
				if (contentType != null) {
					contentType = contentType.toLowerCase();
					if (contentType.contains("charset=")) {
						charsetName = StringUtils.substringAfter(contentType, "charset=").trim();
					}
				}
				logger.log(Level.FINE, "Content-Type charset: {0}", charsetName);
				return new MomentTensor(out.toString(charsetName), connection.getHeaderField("Etag"));
			}
		}
	}

}
