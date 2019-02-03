package it.albertus.eqbulletin.service;

import java.util.Optional;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Bulletin;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.net.FetchException;

public interface BulletinProvider {

	/**
	 * Returns a bulletin based on the provided request parameters.
	 * 
	 * @param request search filters and other job parameters
	 * @return an optional bulletin
	 * @throws FetchException if an error occurs while fetching informations
	 *         from data source
	 * @throws DecodeException if an error occurs while decoding earthquake
	 *         informations fetched from data source
	 */
	Optional<Bulletin> getBulletin(SearchRequest request, BooleanSupplier canceled) throws FetchException, DecodeException;

	/** Requests the cancellation of the operation. */
	void cancel();

}
