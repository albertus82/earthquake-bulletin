package io.github.albertus82.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import io.github.albertus82.eqbulletin.model.BeachBall;
import io.github.albertus82.eqbulletin.model.Earthquake;
import io.github.albertus82.eqbulletin.service.GeofonUtils;
import it.albertus.util.IOUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BeachBallDownloader extends StaticResourceDownloader<BeachBall> {

	private static final short BUFFER_SIZE = 0x400;

	public BeachBallDownloader() {
		super("image/png,image/*;q=0.9,*/*;q=0.8");
	}

	public Optional<BeachBall> download(final Earthquake earthquake, final BooleanSupplier canceled) throws IOException {
		try {
			return Optional.ofNullable(doDownload(getUrl(earthquake), canceled));
		}
		catch (final IOException | URISyntaxException e) {
			log.error("Cannot construct beach ball URL for " + earthquake + ':', e);
			return Optional.empty();
		}
	}

	public Optional<BeachBall> download(final Earthquake earthquake, final BeachBall cached, final BooleanSupplier canceled) throws IOException {
		try {
			return Optional.ofNullable(doDownload(getUrl(earthquake), cached, canceled));
		}
		catch (final IOException | URISyntaxException e) {
			log.error("Cannot construct beach ball URL for " + earthquake + ':', e);
			return Optional.empty();
		}
	}

	@Override
	protected BeachBall makeObject(final InputStream in, final URLConnection connection) throws IOException {
		try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			IOUtils.copy(in, out, BUFFER_SIZE);
			return new BeachBall(out.toByteArray(), connection.getHeaderField("Etag"));
		}
	}

	private static URL getUrl(final Earthquake earthquake) throws MalformedURLException, URISyntaxException {
		return GeofonUtils.getBeachBallUri(earthquake.getGuid(), earthquake.getTime().getYear()).toURL();
	}

}
