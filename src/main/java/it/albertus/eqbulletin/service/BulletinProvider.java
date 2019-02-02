package it.albertus.eqbulletin.service;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.net.FetchException;

public interface BulletinProvider {

	/**
	 * Returns a list of earthquakes based on the provided request parameters.
	 * 
	 * @param request search filters and other job parameters
	 * @return an optional collection of earthquakes
	 * @throws FetchException if an error occurs while fetching informations
	 *         from data source
	 * @throws DecodeException if an error occurs while decoding earthquake
	 *         informations fetched from data source
	 */
	Optional<Collection<Earthquake>> getEarthquakes(SearchRequest request, BooleanSupplier canceled) throws FetchException, DecodeException;

	/** Requests the cancellation of the operation. */
	void cancel();

}
