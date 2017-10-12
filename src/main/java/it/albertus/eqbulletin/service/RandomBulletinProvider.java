package it.albertus.eqbulletin.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;

public class RandomBulletinProvider implements BulletinProvider {

	@Override
	public List<Earthquake> getEarthquakes(final SearchJobVars jobVariables) {
		final List<Earthquake> earthquakes = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			final Calendar date = Calendar.getInstance();
			date.setLenient(false);
			date.add(Calendar.HOUR_OF_DAY, -1 * (new Random().nextInt(24) + 1));
			date.add(Calendar.MINUTE, new Random().nextInt(30));
			date.add(Calendar.SECOND, new Random().nextInt(30));
			final String uuid = UUID.randomUUID().toString();
			final float latitude = (new Random().nextInt(18000) - 9000) / 100f;
			final float longitude = (new Random().nextInt(36000) - 18000) / 100f;
			final float magnitude = (new Random().nextInt(70) + 20) / 10f;
			final int depth = new Random().nextInt(700);
			final Status status = Status.values()[new Random().nextInt(Status.values().length)];
			earthquakes.add(new Earthquake(uuid, date.getTime(), magnitude, new Latitude(latitude), new Longitude(longitude), new Depth(depth), status, uuid, null, null));
		}
		Collections.sort(earthquakes);
		return earthquakes;
	}

	@Override
	public void cancel() {/* Ignore */}

}
