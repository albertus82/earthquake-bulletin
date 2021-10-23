package io.github.albertus82.eqbulletin.service.net;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import io.github.albertus82.eqbulletin.model.Bulletin;
import io.github.albertus82.eqbulletin.service.SearchRequest;
import io.github.albertus82.eqbulletin.service.decode.DecodeException;

public interface BulletinDownloader {

	Optional<Bulletin> download(SearchRequest request, BooleanSupplier canceled) throws FetchException, DecodeException;

	void cancel();

}
