package io.github.albertus82.eqbulletin.service;

import java.util.ArrayList;
import java.util.List;

import io.github.albertus82.eqbulletin.model.Coordinates;
import io.github.albertus82.eqbulletin.model.Depth;
import io.github.albertus82.eqbulletin.model.Latitude;
import io.github.albertus82.eqbulletin.model.Longitude;

public class AreaCalculator {

	long max = 708822568579L;

	public static void main(String[] args) {
		List<Coordinates> l = new ArrayList<>();
		for (float lat = 0; lat <= 180; lat++) {
			//		float lat = 56;
			float lon = 50;
			int off = 1;

			l.add(new Coordinates(Latitude.valueOf(lat), Longitude.valueOf(lon)));
			l.add(new Coordinates(Latitude.valueOf(lat), Longitude.valueOf(lon + off)));//1
			l.add(new Coordinates(Latitude.valueOf(lat + off), Longitude.valueOf(lon + off)));
			l.add(new Coordinates(Latitude.valueOf(lat + off), Longitude.valueOf(lon)));//2
			l.add(new Coordinates(Latitude.valueOf(lat), Longitude.valueOf(lon)));
			System.out.println((long) calculatePolygonArea(l));
		}
	}

	public static double calculatePolygonArea(final List<Coordinates> coordinates) {
		final int r = Depth.EARTH_RADIUS_METERS;
		double area = 0;

		if (coordinates.size() > 2) {
			for (int i = 0; i < coordinates.size() - 1; i++) {
				final Coordinates p1 = coordinates.get(i);
				final Coordinates p2 = coordinates.get(i + 1);
				area += Math.toRadians(p2.getLongitude().doubleValue() - p1.getLongitude().doubleValue()) * (2 + Math.sin(Math.toRadians(p1.getLatitude().doubleValue())) + Math.sin(Math.toRadians(p2.getLatitude().doubleValue())));
			}
			area = area * r * r / 2;
		}

		return Math.abs(area);
	}

}
