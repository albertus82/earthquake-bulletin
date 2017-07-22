package it.albertus.earthquake.gui.job;

import java.util.Collection;

import it.albertus.earthquake.model.Earthquake;

public interface BulletinProvider {

	Collection<Earthquake> getEarthquakes(SearchJobVars jobVariables) throws FetchException, DecodeException;

}
