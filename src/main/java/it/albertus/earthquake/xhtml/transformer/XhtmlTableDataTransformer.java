package it.albertus.earthquake.xhtml.transformer;

import it.albertus.earthquake.EarthquakeBulletin;
import it.albertus.earthquake.model.Depth;
import it.albertus.earthquake.model.Earthquake;
import it.albertus.earthquake.model.Latitude;
import it.albertus.earthquake.model.Longitude;
import it.albertus.earthquake.model.Status;
import it.albertus.earthquake.xhtml.TableData;
import it.albertus.util.NewLine;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

public class XhtmlTableDataTransformer {

	private static final String guidPrefix = "id=";
	private static final String guidSuffix = "'>";
	private static final String timePrefix = guidSuffix;
	private static final String timeSuffix = "</a";
	private static final String magnitudePrefix = ">";
	private static final String magnitudeSuffix = "</";
	private static final String coordinatesPrefix = magnitudePrefix;
	private static final String coordinatesSuffix = "&deg;";
	private static final String depthSuffix = magnitudeSuffix;
	private static final String statusPrefix = guidSuffix;
	private static final String statusSuffix = magnitudeSuffix;
	private static final String regionPrefix = statusPrefix;
	private static final String regionSuffix = magnitudeSuffix;

	/** Use {@link #parseRssDate} method instead. */
	@Deprecated
	private static final DateFormat xhtmlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		xhtmlDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private static synchronized Date parseXhtmlDate(final String source) {
		try {
			return xhtmlDateFormat.parse(source);
		}
		catch (final ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}

	public static Set<Earthquake> fromXhtml(final TableData tableData) throws IllegalArgumentException {
		final Set<Earthquake> earthquakes = new TreeSet<>();
		if (tableData != null && tableData.getItems().size() > 1) {
			// Discards first and last <td>
			for (int index = 1; index < tableData.getItems().size() - 1; index++) {
				final Earthquake converted = fromXhtml(tableData.getItems().get(index));
				if (converted != null) {
					earthquakes.add(converted);
				}
			}
		}
		return earthquakes;
	}

	private static Earthquake fromXhtml(final String td) throws IllegalArgumentException {
		try {
			final String lines[] = td.split(NewLine.SYSTEM_LINE_SEPARATOR);
			final Calendar time = Calendar.getInstance();
			time.setTime(parseXhtmlDate(lines[0].substring(lines[0].lastIndexOf(timePrefix) + timePrefix.length(), lines[0].indexOf(timeSuffix)).trim()));
			final String guid = lines[0].substring(lines[0].indexOf(guidPrefix) + guidPrefix.length(), lines[0].lastIndexOf(guidSuffix)).trim();
			final float magnitude = Float.parseFloat(lines[1].substring(lines[1].indexOf(magnitudePrefix) + magnitudePrefix.length(), lines[1].indexOf(magnitudeSuffix)).trim());
			final float latitude = Float.parseFloat(lines[2].substring(lines[2].indexOf(coordinatesPrefix) + coordinatesPrefix.length(), lines[2].indexOf(coordinatesSuffix)).trim());
			final float longitude = Float.parseFloat(lines[3].substring(lines[3].indexOf(coordinatesPrefix) + coordinatesPrefix.length(), lines[3].indexOf(coordinatesSuffix)).trim());
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
