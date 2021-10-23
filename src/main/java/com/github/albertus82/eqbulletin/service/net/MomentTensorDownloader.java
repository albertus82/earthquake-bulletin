package com.github.albertus82.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Optional;

import com.github.albertus82.eqbulletin.model.Earthquake;
import com.github.albertus82.eqbulletin.model.MomentTensor;

import it.albertus.util.IOUtils;

public class MomentTensorDownloader extends StaticResourceDownloader<MomentTensor> {

	private static final short BUFFER_SIZE = 0x200;

	public MomentTensorDownloader() {
		super("text/plain,text/*;q=0.9,*/*;q=0.8");
	}

	public Optional<MomentTensor> download(final Earthquake earthquake) throws IOException {
		return Optional.ofNullable(doDownload(getUrl(earthquake)));
	}

	public Optional<MomentTensor> download(final Earthquake earthquake, final MomentTensor cached) throws IOException {
		return Optional.ofNullable(doDownload(getUrl(earthquake), cached));
	}

	@Override
	protected MomentTensor makeObject(final InputStream in, final URLConnection connection) throws IOException {
		final Charset charset = ConnectionUtils.detectCharset(connection);
		try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			IOUtils.copy(in, out, BUFFER_SIZE);
			return new MomentTensor(out.toString(charset.name()), connection.getHeaderField("Etag"));
		}
	}

	private static URL getUrl(final Earthquake earthquake) throws MalformedURLException {
		return earthquake.getMomentTensorUri().orElseThrow(IllegalStateException::new).toURL();
	}

}
