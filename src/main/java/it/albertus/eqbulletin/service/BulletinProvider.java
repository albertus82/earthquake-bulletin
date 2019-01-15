package it.albertus.eqbulletin.service;

import java.util.Collection;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.service.decode.DecodeException;
import it.albertus.eqbulletin.service.net.FetchException;

public interface BulletinProvider {

	/**
	 * Returns a list of earthquakes based on the provided request parameters.
	 * 
	 * @param request search filters and other job parameters
	 * @return a list of earthquakes
	 * @throws FetchException if an error occurs while fetching informations
	 *         from data source
	 * @throws DecodeException if an error occurs while decoding earthquake
	 *         informations fetched from data source
	 * @throws CancelException if the operation is canceled by the user
	 */
	Collection<Earthquake> getEarthquakes(SearchRequest request, BooleanSupplier canceled) throws FetchException, DecodeException, CancelException;

	/** Requests the cancellation of the operation. */
	void cancel();

}
