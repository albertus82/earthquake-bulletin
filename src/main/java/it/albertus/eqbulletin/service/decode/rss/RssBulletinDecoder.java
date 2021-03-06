package it.albertus.eqbulletin.service.decode.rss;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.GeofonUtils;
import it.albertus.eqbulletin.service.decode.rss.xml.Item;
import it.albertus.eqbulletin.service.decode.rss.xml.RssBulletin;
import it.albertus.util.logging.LoggerFactory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RssBulletinDecoder {

	private static final Logger logger = LoggerFactory.getLogger(RssBulletinDecoder.class);

	private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").parseDefaulting(ChronoField.MILLI_OF_SECOND, 0).toFormatter().withZone(ZoneOffset.UTC);

	public static List<Earthquake> decode(final RssBulletin data) {
		if (data != null && data.getChannel() != null && data.getChannel().getItems() != null) {
			final List<Earthquake> earthquakes = new ArrayList<>();
			for (final Item item : data.getChannel().getItems()) {
				earthquakes.add(decodeItem(item));
			}
			return earthquakes;
		}
		else {
			return Collections.emptyList();
		}
	}

	private static Earthquake decodeItem(final Item item) {
		final String guid = item.getGuid().getContent().trim();

		// Title
		final String title = item.getTitle();
		final float magnitudo = Float.parseFloat(title.substring(1, title.indexOf(',')).trim());
		final String region = title.substring(title.indexOf(',') + 1).trim();

		// Description
		final String[] descriptionTokens = item.getDescription().split("\\s+");
		final ZonedDateTime time = dateTimeFormatter.parse(descriptionTokens[0].trim() + ' ' + descriptionTokens[1].trim(), ZonedDateTime::from);
		final float latitude = Float.parseFloat(descriptionTokens[2].trim());
		final float longitude = Float.parseFloat(descriptionTokens[3].trim());
		final short depth = Short.parseShort(descriptionTokens[4].trim());
		final Status status = Status.valueOf(descriptionTokens[6].trim());

		// URIs
		URI link = null;
		final String pageUrl = item.getLink();
		if (pageUrl != null) {
			try {
				link = new URI(pageUrl.trim());
			}
			catch (final URISyntaxException e) {
				logger.log(Level.WARNING, Messages.get("err.url.malformed", pageUrl), e);
			}
		}

		URI enclosureUri = null;
		final String imageUrl = item.getEnclosure() != null ? item.getEnclosure().getUrl() : null;
		if (imageUrl != null) {
			try {
				enclosureUri = new URI(imageUrl.trim());
			}
			catch (final URISyntaxException e) {
				logger.log(Level.WARNING, Messages.get("err.url.malformed", imageUrl), e);
			}
		}

		URI momentTensorUri = null;
		if (item.getMt() != null && "yes".equalsIgnoreCase(item.getMt().trim())) {
			momentTensorUri = GeofonUtils.getEventMomentTensorUri(guid, time.get(ChronoField.YEAR));
		}

		return new Earthquake(guid, time, magnitudo, Latitude.valueOf(latitude), Longitude.valueOf(longitude), Depth.valueOf(depth), status, region, link, enclosureUri, momentTensorUri);
	}

}
