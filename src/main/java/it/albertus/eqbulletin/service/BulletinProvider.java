package it.albertus.eqbulletin.service;

import java.io.IOException;
import java.util.Collection;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Earthquake;

public interface BulletinProvider {

	/**
	 * Returns a list of earthquakes based on the provided job variables.
	 * 
	 * @param jobVariables job variables, including search filters
	 * @return a list of earthquakes
	 * @throws FetchException if an error occurs while fetching informations
	 *         from data source
	 * @throws DecodeException if an error occurs while decoding earthquake
	 *         informations fetched from data source
	 * @throws CancelException 
	 * @throws IOException 
	 */
	Collection<Earthquake> getEarthquakes(SearchRequest jobVariables, BooleanSupplier canceled) throws FetchException, DecodeException, IOException, CancelException;

	/** Requests the cancellation of the operation. */
	void cancel();

}
