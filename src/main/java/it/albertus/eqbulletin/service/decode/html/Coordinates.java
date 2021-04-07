package it.albertus.eqbulletin.service.decode.html;

import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import lombok.Value;

@Value
public class Coordinates {

	Latitude latitude;
	Longitude longitude;

}
