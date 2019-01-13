package it.albertus.eqbulletin.service.net;

import java.io.IOException;
import java.util.Collection;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.CancelException;
import it.albertus.eqbulletin.service.DecodeException;
import it.albertus.eqbulletin.service.SearchRequest;

public interface BulletinDownloader {

	Collection<Earthquake> download(SearchRequest request, BooleanSupplier canceled) throws IOException, DecodeException, CancelException;

	void cancel();
}
