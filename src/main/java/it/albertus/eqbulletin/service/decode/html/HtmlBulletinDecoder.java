package it.albertus.eqbulletin.service.decode.html;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.service.GeofonUtils;

public class HtmlBulletinDecoder {

	private static final String DEGREE_SIGN = "\u00B0";

	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").parseDefaulting(ChronoField.MILLI_OF_SECOND, 0).toFormatter().withZone(ZoneOffset.UTC);

	public static List<Earthquake> decode(final Document body) {
		final List<Earthquake> earthquakes = new ArrayList<>();
		if (body != null) {
			final Elements rows = body.getElementsByTag("tr");
			for (final Element row : rows.subList(2, rows.size() - 1)) { // Discards first and last <td>
				final Earthquake converted = decodeItem(row);
				earthquakes.add(converted);
			}
		}
		return earthquakes;
	}

	private static Earthquake decodeItem(final Element row) {
		try {
			final ZonedDateTime time = dateTimeFormatter.parse(row.child(0).text(), ZonedDateTime::from);

			final String href = row.child(0).child(0).attr("href");
			final String guid = href.substring(href.indexOf('=') + 1);

			final float magnitude = Float.parseFloat(row.child(1).text());

			final String[] splitLat = row.child(2).text().split(DEGREE_SIGN);
			float latitude = Float.parseFloat(splitLat[0]);
			if ("S".equalsIgnoreCase(splitLat[1])) {
				latitude = -latitude;
			}

			final String[] splitLon = row.child(3).text().split(DEGREE_SIGN);
			float longitude = Float.parseFloat(splitLon[0]);
			if ("W".equalsIgnoreCase(splitLon[1])) {
				longitude = -longitude;
			}

			final short depth = Short.parseShort(row.child(4).text());
			final Status status = Status.valueOf(row.child(5).text());
			final String region = row.child(7).text();

			final URI link = GeofonUtils.getEventPageUri(guid);
			final URI enclosureUri = GeofonUtils.getEventMapUri(guid, time.get(ChronoField.YEAR));

			URI momentTensorUri = null;
			if (row.child(6).html().contains(GeofonUtils.MOMENT_TENSOR_FILENAME) || row.children().size() > 7 && row.child(7).html().contains(GeofonUtils.MOMENT_TENSOR_FILENAME)) {
				momentTensorUri = GeofonUtils.getEventMomentTensorUri(guid, time.get(ChronoField.YEAR));
			}

			return new Earthquake(guid, time, magnitude, new Latitude(latitude), new Longitude(longitude), Depth.valueOf(depth), status, region, link, enclosureUri, momentTensorUri);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(row.toString(), e);
		}
	}

	private HtmlBulletinDecoder() {
		throw new IllegalAccessError();
	}

}
