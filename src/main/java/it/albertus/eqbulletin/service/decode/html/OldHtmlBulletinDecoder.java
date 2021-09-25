package it.albertus.eqbulletin.service.decode.html;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.service.GeofonUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OldHtmlBulletinDecoder extends AbstractHtmlBulletinDecoder {

	@Override
	public List<Earthquake> decode(final Document document) {
		if (document != null) {
			final List<Earthquake> earthquakes = new ArrayList<>();
			final Elements rows = document.getElementsByTag("tr");
			for (final Element row : rows.subList(2, rows.size() - 1)) { // Discards table header & footer
				final Earthquake converted = decodeItem(row);
				earthquakes.add(converted);
			}
			return earthquakes;
		}
		else {
			return Collections.emptyList();
		}
	}

	@Override
	protected Earthquake decodeItem(@NonNull final Element row) {
		try {
			final ZonedDateTime time = decodeTime(row);

			final Optional<Element> eventLink = findFirstLink(row.child(0));
			if (!eventLink.isPresent()) {
				throw new IllegalStateException("Event link not present.");
			}
			final String guid = decodeGuid(eventLink.get());
			final URI link = decodeLink(eventLink.get());
			final float magnitude = decodeMagnitude(row);
			final Coordinates coordinates = decodeCoordinates(row);
			final Depth depth = decodeDepth(row);
			final Status status = decodeStatus(row);
			final String region = decodeRegion(row);
			final URI enclosureUri = decodeEnclosureUri(guid, time.get(ChronoField.YEAR));
			final URI momentTensorUri = decodeMomentTensorUri(row);

			return new Earthquake(guid, time, magnitude, coordinates.getLatitude(), coordinates.getLongitude(), depth, status, region, link, enclosureUri, momentTensorUri);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(row.toString(), e);
		}
	}

	private static ZonedDateTime decodeTime(@NonNull final Element row) {
		return dateTimeFormatter.parse(row.child(0).text(), ZonedDateTime::from);
	}

	private static String decodeGuid(@NonNull final Element anchor) {
		return anchor.attr("href").substring(anchor.attr("href").indexOf('=') + 1).trim();
	}

	private static URI decodeLink(@NonNull final Element anchor) {
		try {
			return GeofonUtils.toURI(anchor.absUrl("href"));
		}
		catch (final MalformedURLException | URISyntaxException e) {
			log.error("Cannot decode link " + anchor + ":", e);
			return null;
		}
	}

	private static float decodeMagnitude(@NonNull final Element row) {
		return Float.parseFloat(row.child(1).text());
	}

	private static Coordinates decodeCoordinates(@NonNull final Element row) {
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

		return new Coordinates(Latitude.valueOf(latitude), Longitude.valueOf(longitude));
	}

	private static Depth decodeDepth(@NonNull final Element row) {
		return Depth.valueOf(Short.parseShort(row.child(4).text()));
	}

	private static Status decodeStatus(@NonNull final Element row) {
		return Status.valueOf(row.child(5).text());
	}

	private static String decodeRegion(@NonNull final Element row) {
		return row.child(7).text().isEmpty() ? row.child(6).text() : row.child(7).text();
	}

	private static URI decodeEnclosureUri(@NonNull final String guid, int year) {
		try {
			return GeofonUtils.getEventMapUri(guid, year);
		}
		catch (final MalformedURLException | URISyntaxException e) {
			log.error("Cannot construct enclosure URI for (guid=" + guid + ", year=" + year + "):", e);
			return null;
		}
	}

	private static URI decodeMomentTensorUri(@NonNull final Element row) {
		for (int i = 6; i < row.children().size(); i++) {
			final Optional<Element> a = findFirstLink(row.child(i));
			if (a.isPresent() && ("MT".equalsIgnoreCase(a.get().text()) || a.get().attr("href").endsWith(GeofonUtils.MOMENT_TENSOR_FILENAME))) {
				try {
					return GeofonUtils.toURI(a.get().absUrl("href"));
				}
				catch (final MalformedURLException | URISyntaxException e) {
					log.error("Cannot construct moment tensor URI for " + row + ":", e);
					break;
				}
			}
		}
		return null;
	}

	private static Optional<Element> findFirstLink(final Element parent) {
		return parent.getElementsByTag("a").stream().filter(e -> e.hasAttr("href")).findFirst();
	}

}
