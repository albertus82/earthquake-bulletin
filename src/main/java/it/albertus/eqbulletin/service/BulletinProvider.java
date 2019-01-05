package it.albertus.eqbulletin.service;

import java.util.List;

import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.util.CancellationStatus;

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
	 */
	List<Earthquake> getEarthquakes(SearchJobVars jobVariables, CancellationStatus status) throws FetchException, DecodeException;

	/** Requests the cancellation of the operation. */
	void cancel();

}
