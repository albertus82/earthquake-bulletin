package it.albertus.eqbulletin.service.decode.html;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.service.GeofonUtils;
import it.albertus.util.NewLine;

public class HtmlBulletinDecoder {

	private static final String GUID_PREFIX = "id=";
	private static final String GUID_SUFFIX = "'>";
	private static final String TIME_PREFIX = GUID_SUFFIX;
	private static final String TIME_SUFFIX = "</a";
	private static final String MAGNITUDE_PREFIX = ">";
	private static final String MAGNITUDE_SUFFIX = "</";
	private static final String LATITUDE_PREFIX = MAGNITUDE_PREFIX;
	private static final String LATITUDE_SUFFIX = "&deg;";
	private static final String LATITUDE_SIGN_PREFIX = LATITUDE_SUFFIX;
	private static final String LATITUDE_SIGN_SUFFIX = MAGNITUDE_SUFFIX;
	private static final String LONGITUDE_PREFIX = LATITUDE_PREFIX;
	private static final String LONGITUDE_SUFFIX = LATITUDE_SUFFIX;
	private static final String LONGITUDE_SIGN_PREFIX = LATITUDE_SUFFIX;
	private static final String LONGITUDE_SIGN_SUFFIX = MAGNITUDE_SUFFIX;
	private static final String DEPTH_SUFFIX = MAGNITUDE_SUFFIX;
	private static final String STATUS_PREFIX = GUID_SUFFIX;
	private static final String STATUS_SUFFIX = MAGNITUDE_SUFFIX;
	private static final String REGION_PREFIX = STATUS_PREFIX;
	private static final String REGION_SUFFIX = MAGNITUDE_SUFFIX;

	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").parseDefaulting(ChronoField.MILLI_OF_SECOND, 0).toFormatter().withZone(ZoneOffset.UTC);

	public static List<Earthquake> decode(final HtmlBulletin data) {
		final List<Earthquake> earthquakes = new ArrayList<>();
		if (data != null && data.getItems().size() > 1) {
			// Discards first and last <td>
			for (int index = 1; index < data.getItems().size() - 1; index++) {
				final Earthquake converted = decodeItem(data.getItems().get(index));
				earthquakes.add(converted);
			}
		}
		return earthquakes;
	}

	private static Earthquake decodeItem(final String td) {
		try {
			final String[] lines = td.split("[" + NewLine.CRLF + "]+");

			final ZonedDateTime time = dateTimeFormatter.parse(lines[0].substring(lines[0].lastIndexOf(TIME_PREFIX) + TIME_PREFIX.length(), lines[0].indexOf(TIME_SUFFIX)).trim(), ZonedDateTime::from);

			final String guid = lines[0].substring(lines[0].indexOf(GUID_PREFIX) + GUID_PREFIX.length(), lines[0].lastIndexOf(GUID_SUFFIX)).trim();

			final float magnitude = Float.parseFloat(lines[1].substring(lines[1].indexOf(MAGNITUDE_PREFIX) + MAGNITUDE_PREFIX.length(), lines[1].indexOf(MAGNITUDE_SUFFIX)).trim());

			float latitude = Float.parseFloat(lines[2].substring(lines[2].indexOf(LATITUDE_PREFIX) + LATITUDE_PREFIX.length(), lines[2].indexOf(LATITUDE_SUFFIX)).trim());
			if ("S".equalsIgnoreCase(lines[2].substring(lines[2].indexOf(LATITUDE_SIGN_PREFIX) + LATITUDE_SIGN_PREFIX.length(), lines[2].indexOf(LATITUDE_SIGN_SUFFIX)).trim())) {
				latitude *= -1;
			}
			float longitude = Float.parseFloat(lines[3].substring(lines[3].indexOf(LONGITUDE_PREFIX) + LONGITUDE_PREFIX.length(), lines[3].indexOf(LONGITUDE_SUFFIX)).trim());
			if ("W".equalsIgnoreCase(lines[3].substring(lines[3].indexOf(LONGITUDE_SIGN_PREFIX) + LONGITUDE_SIGN_PREFIX.length(), lines[3].indexOf(LONGITUDE_SIGN_SUFFIX)).trim())) {
				longitude *= -1;
			}

			final short depth = Short.parseShort(lines[4].substring(lines[4].indexOf(MAGNITUDE_PREFIX) + MAGNITUDE_PREFIX.length(), lines[4].indexOf(DEPTH_SUFFIX)).trim());
			final Status status = Status.valueOf(lines[5].substring(lines[5].lastIndexOf(STATUS_PREFIX) + STATUS_PREFIX.length(), lines[5].indexOf(STATUS_SUFFIX)).trim());
			final String region = lines[6].substring(lines[6].lastIndexOf(REGION_PREFIX) + REGION_PREFIX.length(), lines[6].lastIndexOf(REGION_SUFFIX)).trim();

			final URI link = GeofonUtils.getEventPageUri(guid);
			final URI enclosure = GeofonUtils.getEventMapUri(guid, time.get(ChronoField.YEAR));

			final Earthquake earthquake = new Earthquake(guid, time, magnitude, new Latitude(latitude), new Longitude(longitude), new Depth(depth), status, region, link, enclosure);

			if (lines[6].contains(GeofonUtils.MOMENT_TENSOR_FILENAME) || lines.length > 7 && lines[7].contains(GeofonUtils.MOMENT_TENSOR_FILENAME)) {
				earthquake.setMomentTensorUri(GeofonUtils.getEventMomentTensorUri(guid, time.get(ChronoField.YEAR)));
			}

			return earthquake;
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(td, e);
		}
	}

	private HtmlBulletinDecoder() {
		throw new IllegalAccessError();
	}

}
