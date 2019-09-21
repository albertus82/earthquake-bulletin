package it.albertus.eqbulletin.service.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.MomentTensorImage;
import it.albertus.eqbulletin.service.GeofonUtils;
import it.albertus.util.IOUtils;

public class MomentTensorImageDownloader extends StaticResourceDownloader<MomentTensorImage> {

	private static final short BUFFER_SIZE = 0x400;

	public MomentTensorImageDownloader() {
		super("image/png,image/*;q=0.9,*/*;q=0.8");
	}

	public Optional<MomentTensorImage> download(final Earthquake earthquake, final BooleanSupplier canceled) throws IOException {
		return Optional.ofNullable(doDownload(getUrl(earthquake), canceled));
	}

	public Optional<MomentTensorImage> download(final Earthquake earthquake, final MomentTensorImage cached, final BooleanSupplier canceled) throws IOException {
		return Optional.ofNullable(doDownload(getUrl(earthquake), cached, canceled));
	}

	@Override
	protected MomentTensorImage makeObject(final InputStream in, final URLConnection connection) throws IOException {
		try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			IOUtils.copy(in, out, BUFFER_SIZE);
			return new MomentTensorImage(out.toByteArray(), connection.getHeaderField("Etag"));
		}
	}

	private static URL getUrl(final Earthquake earthquake) throws MalformedURLException {
		return GeofonUtils.getEventMomentTensorImageUri(earthquake.getGuid(), earthquake.getTime().getYear()).toURL();
	}

}
