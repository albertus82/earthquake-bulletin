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

public class HtmlBulletinDecoder {

	private static final String DEGREE_SIGN = "\u00B0";

	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").parseDefaulting(ChronoField.MILLI_OF_SECOND, 0).toFormatter().withZone(ZoneOffset.UTC);

	public static List<Earthquake> decode(final Document document) {
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

	private static Earthquake decodeItem(final Element anchor) {
		try {
			final Elements divs = anchor.getElementsByTag("div");
			final Elements spans = anchor.getElementsByTag("span");

			final ZonedDateTime time = dateTimeFormatter.parse(divs.get(6).textNodes().get(0).text().trim(), ZonedDateTime::from);

			final String guid = anchor.attr("href").substring(anchor.attr("href").indexOf('=') + 1).trim();
			final URI link = GeofonUtils.toURI(anchor.absUrl("href"));

			final float magnitude = Float.parseFloat(spans.first().text());

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

			final Optional<Element> depthSpan = spans.stream().filter(e -> e.hasClass("pull-right")).findFirst();
			final short depth = Short.parseShort(depthSpan.orElseThrow(() -> new IllegalArgumentException(String.valueOf(spans))).text().replace("*", "").trim());
			final Status status = null; // Removed from the web page on 08/10/2019
			final String region = divs.get(4).text();

			final URI enclosureUri = GeofonUtils.getEventMapUri(guid, time.get(ChronoField.YEAR));

			URI momentTensorUri = null;
			for (final Element img : anchor.getElementsByTag("img")) {
				if ("MT".equalsIgnoreCase(img.attr("alt"))) {
					momentTensorUri = GeofonUtils.getEventMomentTensorUri(guid, time.get(ChronoField.YEAR));
					break;
				}
			}

			return new Earthquake(guid, time, magnitude, Latitude.valueOf(latitude), Longitude.valueOf(longitude), Depth.valueOf(depth), status, region, link, enclosureUri, momentTensorUri);
		}
		catch (final Exception e) {
			throw new IllegalArgumentException(anchor.toString(), e);
		}
	}

	private HtmlBulletinDecoder() {
		throw new IllegalAccessError();
	}

}
