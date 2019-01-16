package it.albertus.eqbulletin.service.net;

import java.util.Collection;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.SearchRequest;
import it.albertus.eqbulletin.service.decode.DecodeException;

public interface BulletinDownloader {

	Collection<Earthquake> download(SearchRequest request, BooleanSupplier canceled) throws FetchException, DecodeException, InterruptedException;

	void cancel();
}
