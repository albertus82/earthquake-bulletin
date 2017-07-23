package it.albertus.earthquake.service;

import java.util.List;

import it.albertus.earthquake.model.Earthquake;

public interface BulletinProvider {

	List<Earthquake> getEarthquakes(SearchJobVars jobVariables) throws FetchException, DecodeException;

}
