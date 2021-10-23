package io.github.albertus82.eqbulletin.service.decode.html;

import io.github.albertus82.eqbulletin.model.Latitude;
import io.github.albertus82.eqbulletin.model.Longitude;
import lombok.Value;

@Value
public class Coordinates {

	Latitude latitude;
	Longitude longitude;

}
