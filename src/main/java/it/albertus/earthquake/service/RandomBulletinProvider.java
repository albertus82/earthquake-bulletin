package it.albertus.earthquake.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import it.albertus.earthquake.model.Depth;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.Latitude;
import it.albertus.earthquake.model.Longitude;
import it.albertus.earthquake.model.Status;

public class RandomBulletinProvider implements BulletinProvider {

	@Override
	public Collection<Earthquake> getEarthquakes(final SearchJobVars jobVariables) {
		final Set<Earthquake> earthquakes = new TreeSet<>();
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
			earthquakes.add(new Earthquake(uuid, date.getTime(), magnitude, new Latitude(latitude), new Longitude(longitude), new Depth(depth), Status.C, uuid, null, null));
		}
		return earthquakes;
	}

}
