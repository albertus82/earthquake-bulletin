package com.github.albertus82.eqbulletin.service;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import com.github.albertus82.eqbulletin.model.Bulletin;
import com.github.albertus82.eqbulletin.service.decode.DecodeException;
import com.github.albertus82.eqbulletin.service.decode.html.HtmlBulletinDecoderFactory;
import com.github.albertus82.eqbulletin.service.net.BulletinDownloader;
import com.github.albertus82.eqbulletin.service.net.FetchException;
import com.github.albertus82.eqbulletin.service.net.HtmlBulletinDownloader;
import com.github.albertus82.eqbulletin.service.net.RssBulletinDownloader;

import lombok.NonNull;

public class GeofonBulletinProvider implements BulletinProvider {

	private BulletinDownloader downloader;

	@Override
	public Optional<Bulletin> getBulletin(@NonNull final SearchRequest request, final BooleanSupplier canceled) throws FetchException, DecodeException {
		switch (request.getFormat()) {
		case HTML:
			downloader = new HtmlBulletinDownloader(HtmlBulletinDecoderFactory.newInstance());
			return downloader.download(request, canceled);
		case RSS:
			downloader = new RssBulletinDownloader();
			return downloader.download(request, canceled);
		default:
			throw new UnsupportedOperationException(request.toString());
		}
	}

	@Override
	public void cancel() {
		if (downloader != null) {
			downloader.cancel();
		}
	}

}
