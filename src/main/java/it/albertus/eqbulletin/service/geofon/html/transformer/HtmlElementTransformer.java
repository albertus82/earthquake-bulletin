package it.albertus.eqbulletin.service.geofon.html.transformer;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.service.geofon.GeofonBulletinProvider;
import it.albertus.util.Configuration;

public class HtmlElementTransformer {

	private static final String DEGREE_SIGN = "\u00B0";
	private static final String MOMENT_TENSOR_FILENAME = "mt.txt";

	private static final ThreadLocal<DateFormat> htmlDateFormat = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			return dateFormat;
		}
	};

	private static final Configuration configuration = EarthquakeBulletinConfig.getInstance();

	private HtmlElementTransformer() {
		throw new IllegalAccessError();
	}

	private static Date parseHtmlDate(final String source) {
		try {
			return htmlDateFormat.get().parse(source);
		}
		catch (final ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}

	public static List<Earthquake> fromHtml(final Element html) {
		final List<Earthquake> earthquakes = new ArrayList<>();
		Elements rows = html.getElementsByTag("tr");
		for (final Element row : rows.subList(2, rows.size() - 1)) {
			final Earthquake converted = parseRow(row);
			earthquakes.add(converted);
		}
		return earthquakes;
	}

	private static Earthquake parseRow(final Element row) {
		try {
			final Calendar time = Calendar.getInstance();
			time.setTime(parseHtmlDate(row.child(0).text()));

			final String href = row.child(0).child(0).attr("href");
			final String guid = href.substring(href.indexOf('=') + 1);

			final float magnitude = Float.parseFloat(row.child(1).text());

			final String[] splitLat = row.child(2).text().split(DEGREE_SIGN);
			float latitude = Float.parseFloat(splitLat[0]);
			if ("S".equalsIgnoreCase(splitLat[1])) {
				latitude *= -1;
			}

			final String[] splitLon = row.child(3).text().split(DEGREE_SIGN);
			float longitude = Float.parseFloat(splitLon[0]);
			if ("W".equalsIgnoreCase(splitLon[1])) {
				longitude *= -1;
			}

			final short depth = Short.parseShort(row.child(4).text());

			final Status status = Status.valueOf(row.child(5).text());

			final String region = row.child(7).text();

			final String baseUrl = configuration.getString("url.base", GeofonBulletinProvider.DEFAULT_BASE_URL);
			final URL link = new URL(baseUrl + "/eqinfo/event.php?id=" + guid);
			final String eventBaseUrl = baseUrl + "/data/alerts/" + time.get(Calendar.YEAR) + "/" + guid + "/";
			final URL enclosure = new URL(eventBaseUrl + guid + ".jpg");

			final Earthquake earthquake = new Earthquake(guid, time.getTime(), magnitude, new Latitude(latitude), new Longitude(longitude), new Depth(depth), status, region, link, enclosure);

			if (MOMENT_TENSOR_FILENAME.equalsIgnoreCase(row.child(6).text())) {
				earthquake.setMomentTensor(new URL(eventBaseUrl + MOMENT_TENSOR_FILENAME));
			}

			return earthquake;
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(String.valueOf(row), e);
		}
	}

}
