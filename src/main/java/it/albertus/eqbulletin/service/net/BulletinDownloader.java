package it.albertus.eqbulletin.service.net;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Bulletin;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;

public interface BulletinDownloader {

	Optional<Bulletin> download(SearchRequest request, BooleanSupplier canceled) throws FetchException, DecodeException;

	void cancel();

}
