package it.albertus.earthquake.html.transformer;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.html.TableData;
import it.albertus.earthquake.model.Depth;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.Latitude;
import it.albertus.earthquake.model.Longitude;
import it.albertus.earthquake.model.Status;
import it.albertus.util.NewLine;

public class HtmlTableDataTransformer {

	private static final String guidPrefix = "id=";
	private static final String guidSuffix = "'>";
	private static final String timePrefix = guidSuffix;
	private static final String timeSuffix = "</a";
	private static final String magnitudePrefix = ">";
	private static final String magnitudeSuffix = "</";
	private static final String latitudePrefix = magnitudePrefix;
	private static final String latitudeSuffix = "&deg;";
	private static final String latitudeSignPrefix = latitudeSuffix;
	private static final String latitudeSignSuffix = magnitudeSuffix;
	private static final String longitudePrefix = latitudePrefix;
	private static final String longitudeSuffix = latitudeSuffix;
	private static final String longitudeSignPrefix = latitudeSuffix;
	private static final String longitudeSignSuffix = magnitudeSuffix;
	private static final String depthSuffix = magnitudeSuffix;
	private static final String statusPrefix = guidSuffix;
	private static final String statusSuffix = magnitudeSuffix;
	private static final String regionPrefix = statusPrefix;
	private static final String regionSuffix = magnitudeSuffix;

	private static final ThreadLocal<DateFormat> htmlDateFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			final DateFormat htmlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			htmlDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			return htmlDateFormat;
		};
	};

	private static Date parseHtmlDate(final String source) {
		try {
			return htmlDateFormat.get().parse(source);
		}
		catch (final ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}

	public static Set<Earthquake> fromHtml(final TableData tableData) throws IllegalArgumentException {
		final Set<Earthquake> earthquakes = new TreeSet<>();
		if (tableData != null && tableData.getItems().size() > 1) {
			// Discards first and last <td>
			for (int index = 1; index < tableData.getItems().size() - 1; index++) {
				final Earthquake converted = fromHtml(tableData.getItems().get(index));
				if (converted != null) {
					earthquakes.add(converted);
				}
			}
		}
		return earthquakes;
	}

	private static Earthquake fromHtml(final String td) throws IllegalArgumentException {
		try {
			final String lines[] = td.split(NewLine.SYSTEM_LINE_SEPARATOR);

			final Calendar time = Calendar.getInstance();
			time.setTime(parseHtmlDate(lines[0].substring(lines[0].lastIndexOf(timePrefix) + timePrefix.length(), lines[0].indexOf(timeSuffix)).trim()));

			final String guid = lines[0].substring(lines[0].indexOf(guidPrefix) + guidPrefix.length(), lines[0].lastIndexOf(guidSuffix)).trim();

			final float magnitude = Float.parseFloat(lines[1].substring(lines[1].indexOf(magnitudePrefix) + magnitudePrefix.length(), lines[1].indexOf(magnitudeSuffix)).trim());

			float latitude = Float.parseFloat(lines[2].substring(lines[2].indexOf(latitudePrefix) + latitudePrefix.length(), lines[2].indexOf(latitudeSuffix)).trim());
			if ("S".equalsIgnoreCase(lines[2].substring(lines[2].indexOf(latitudeSignPrefix) + latitudeSignPrefix.length(), lines[2].indexOf(latitudeSignSuffix)).trim())) {
				latitude *= -1;
			}

			float longitude = Float.parseFloat(lines[3].substring(lines[3].indexOf(longitudePrefix) + longitudePrefix.length(), lines[3].indexOf(longitudeSuffix)).trim());
			if ("W".equalsIgnoreCase(lines[3].substring(lines[3].indexOf(longitudeSignPrefix) + longitudeSignPrefix.length(), lines[3].indexOf(longitudeSignSuffix)).trim())) {
				longitude *= -1;
			}

			final int depth = Integer.parseInt(lines[4].substring(lines[4].indexOf(magnitudePrefix) + magnitudePrefix.length(), lines[4].indexOf(depthSuffix)).trim());

			final Status status = Status.valueOf(lines[5].substring(lines[5].lastIndexOf(statusPrefix) + statusPrefix.length(), lines[5].indexOf(statusSuffix)).trim());

			final String region = lines[6].substring(lines[6].lastIndexOf(regionPrefix) + regionPrefix.length(), lines[6].lastIndexOf(regionSuffix)).trim();

			final URL link = new URL(EarthquakeBulletin.BASE_URL + "/eqinfo/event.php?id=" + guid);

			final URL enclosure = new URL(EarthquakeBulletin.BASE_URL + "/data/alerts/" + time.get(Calendar.YEAR) + "/" + guid + "/" + guid + ".jpg");

			return new Earthquake(guid, time.getTime(), magnitude, new Latitude(latitude), new Longitude(longitude), new Depth(depth), status, region, link, enclosure);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(td, e);
		}
	}

}
