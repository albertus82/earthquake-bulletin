package com.github.albertus82.eqbulletin.service.net;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import com.github.albertus82.eqbulletin.model.Bulletin;
import com.github.albertus82.eqbulletin.service.SearchRequest;
import com.github.albertus82.eqbulletin.service.decode.DecodeException;

public interface BulletinDownloader {

	Optional<Bulletin> download(SearchRequest request, BooleanSupplier canceled) throws FetchException, DecodeException;

	void cancel();

}
