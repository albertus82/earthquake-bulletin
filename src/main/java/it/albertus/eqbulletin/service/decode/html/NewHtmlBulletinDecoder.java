package it.albertus.eqbulletin.service.decode.html;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

public class NewHtmlBulletinDecoder extends AbstractHtmlBulletinDecoder {

	private static final String DEGREE_SIGN = "\u00B0";

	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").parseDefaulting(ChronoField.MILLI_OF_SECOND, 0).toFormatter().withZone(ZoneOffset.UTC);

	@Override
	public List<Earthquake> decode(final Document document) {
		if (document != null) {
			final List<Earthquake> earthquakes = new ArrayList<>();
			final Elements elements = document.getElementsByClass("eqlist");
			if (elements.size() > 1) {
				throw new IllegalStateException();
			}
			for (final Element element : elements) {
				final Collection<Element> anchors = element.getElementsByTag("a").stream().filter(a -> !a.classNames().contains("external-link") && !a.classNames().contains("alert-link") && a.hasAttr("href")).collect(Collectors.toList());
				for (final Element anchor : anchors) {
					final Earthquake converted = decodeItem(anchor);
					earthquakes.add(converted);
				}
			}
			return earthquakes;
		}
		else {
			return Collections.emptyList();
		}
	}

	@Override
	protected Earthquake decodeItem(@NonNull final Element anchor) {
		try {
			final Elements divs = anchor.getElementsByTag("div");
			final Elements spans = anchor.getElementsByTag("span");

			final ZonedDateTime time = decodeTime(divs);
			final String guid = decodeGuid(anchor);
			final URI link = decodeLink(anchor);
			final float magnitude = decodeMagnitude(spans);
			float[] lonLat = decodeLonLat(divs);
			final short depth = decodeDepth(spans);
			final Status status = null; // the status was sadly removed from the HTML page on 2019-10-08
			final String region = decodeRegion(divs);
			final int year = time.get(ChronoField.YEAR);
			final URI enclosureUri = decodeEnclosureUri(guid, year);
			final URI momentTensorUri = decodeMomentTensorUri(anchor, guid, year);

			return new Earthquake(guid, time, magnitude, Latitude.valueOf(lonLat[1]), Longitude.valueOf(lonLat[0]), Depth.valueOf(depth), status, region, link, enclosureUri, momentTensorUri);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(anchor.toString(), e);
		}
	}

	private static ZonedDateTime decodeTime(@NonNull final Elements divs) {
		return dateTimeFormatter.parse(divs.get(6).textNodes().get(0).text().trim(), ZonedDateTime::from);
	}

	private static String decodeGuid(@NonNull final Element anchor) {
		return anchor.attr("href").substring(anchor.attr("href").indexOf('=') + 1).trim();
	}

	private static URI decodeLink(@NonNull final Element anchor) {
		return GeofonUtils.toURI(anchor.absUrl("href"));
	}

	private static float decodeMagnitude(@NonNull final Elements spans) {
		return Float.parseFloat(spans.first().text());
	}

	private static float[] decodeLonLat(@NonNull final Elements divs) {
		final String[] lonLat = divs.get(4).attr("title").split(",");

		final String[] splitLat = lonLat[1].split(DEGREE_SIGN);
		float latitude = Float.parseFloat(splitLat[0]);
		if ("S".equalsIgnoreCase(splitLat[1])) {
			latitude = -latitude;
		}

		final String[] splitLon = lonLat[0].split(DEGREE_SIGN);
		float longitude = Float.parseFloat(splitLon[0]);
		if ("W".equalsIgnoreCase(splitLon[1])) {
			longitude = -longitude;
		}

		return new float[] { longitude, latitude };
	}

	private static short decodeDepth(@NonNull final Elements spans) {
		final Optional<Element> depthSpan = spans.stream().filter(e -> e.hasClass("pull-right")).findFirst();
		return Short.parseShort(depthSpan.orElseThrow(() -> new IllegalArgumentException(String.valueOf(spans))).text().replace("*", "").trim());
	}

	private static String decodeRegion(@NonNull final Elements divs) {
		return divs.get(4).text();
	}

	private static URI decodeEnclosureUri(@NonNull final String guid, final int year) {
		return GeofonUtils.getEventMapUri(guid, year);
	}

	private static URI decodeMomentTensorUri(@NonNull final Element anchor, @NonNull final String guid, final int year) {
		for (final Element img : anchor.getElementsByTag("img")) {
			if ("MT".equalsIgnoreCase(img.attr("alt"))) {
				return GeofonUtils.getEventMomentTensorUri(guid, year);
			}
		}
		return null;
	}

}
