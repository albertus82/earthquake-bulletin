package it.albertus.eqbulletin.service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

import it.albertus.eqbulletin.model.Bulletin;
import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;

public class RandomBulletinProvider implements BulletinProvider {

	@Override
	public Optional<Bulletin> getBulletin(final SearchRequest jobVariables, final BooleanSupplier canceled) {
		final List<Earthquake> earthquakes = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			final Calendar date = Calendar.getInstance();
			date.setLenient(false);
			final ThreadLocalRandom random = ThreadLocalRandom.current();
			date.add(Calendar.HOUR_OF_DAY, -1 * (random.nextInt(24) + 1));
			date.add(Calendar.MINUTE, random.nextInt(30));
			date.add(Calendar.SECOND, random.nextInt(30));
			final String uuid = UUID.randomUUID().toString();
			final float latitude = (random.nextInt(18000) - 9000) / 100f;
			final float longitude = (random.nextInt(36000) - 18000) / 100f;
			final float magnitude = (random.nextInt(70) + 20) / 10f;
			final short depth = (short) random.nextInt(700);
			final Status status = Status.values()[random.nextInt(Status.values().length)];
			earthquakes.add(new Earthquake(uuid, date.getTime().toInstant().atZone(ZoneId.of("UTC")), magnitude, new Latitude(latitude), new Longitude(longitude), new Depth(depth), status, uuid, null, null, null));
		}
		Collections.sort(earthquakes);
		return Optional.of(new Bulletin(earthquakes));
	}

	@Override
	public void cancel() {/* Ignore */}

}
