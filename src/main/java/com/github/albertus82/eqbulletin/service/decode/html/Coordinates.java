package com.github.albertus82.eqbulletin.service.decode.html;

import com.github.albertus82.eqbulletin.model.Latitude;
import com.github.albertus82.eqbulletin.model.Longitude;

import lombok.Value;

@Value
public class Coordinates {

	Latitude latitude;
	Longitude longitude;

}
