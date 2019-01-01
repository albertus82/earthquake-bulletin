package it.albertus.eqbulletin.service.geofon.rss.transformer;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.albertus.eqbulletin.model.Depth;
import it.albertus.eqbulletin.model.Earthquake;
import it.albertus.eqbulletin.model.Latitude;
import it.albertus.eqbulletin.model.Longitude;
import it.albertus.eqbulletin.model.Status;
import it.albertus.eqbulletin.resources.Messages;
import it.albertus.eqbulletin.service.geofon.rss.xml.Item;
import it.albertus.eqbulletin.service.geofon.rss.xml.Rss;
import it.albertus.util.logging.LoggerFactory;

public class RssItemTransformer {

	private static final Logger logger = LoggerFactory.getLogger(RssItemTransformer.class);

	private static final ThreadLocal<DateFormat> rssDateFormat = ThreadLocal.withInitial(() -> {
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormat;
	});

	private RssItemTransformer() {
		throw new IllegalAccessError();
	}

	private static Date parseRssDate(final String source) {
		try {
			return rssDateFormat.get().parse(source);
		}
		catch (final ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}

	public static List<Earthquake> fromRss(final Rss rss) {
		final List<Earthquake> earthquakes = new ArrayList<>();
		if (rss != null && rss.getChannel() != null && rss.getChannel().getItem() != null) {
			for (final Item item : rss.getChannel().getItem()) {
				earthquakes.add(RssItemTransformer.fromRss(item));
			}
		}
		return earthquakes;
	}

	private static Earthquake fromRss(final Item rssItem) {
		// Title
		final String title = rssItem.getTitle();
		final float magnitudo = Float.parseFloat(title.substring(1, title.indexOf(',')).trim());
		final String region = title.substring(title.indexOf(',') + 1).trim();

		// Description
		final String[] descriptionTokens = rssItem.getDescription().split("\\s+");
		final Date time = parseRssDate(descriptionTokens[0].trim() + ' ' + descriptionTokens[1].trim());
		final float latitude = Float.parseFloat(descriptionTokens[2].trim());
		final float longitude = Float.parseFloat(descriptionTokens[3].trim());
		final short depth = Short.parseShort(descriptionTokens[4].trim());
		final Status status = Status.valueOf(descriptionTokens[6].trim());

		// Links
		URL link = null;
		final String pageUrl = rssItem.getLink();
		if (pageUrl != null) {
			try {
				link = new URL(pageUrl.trim());
			}
			catch (final MalformedURLException mue) {
				logger.log(Level.WARNING, Messages.get("err.url.malformed", pageUrl), mue);
			}
		}

		URL enclosure = null;
		final String imageUrl = rssItem.getEnclosure() != null ? rssItem.getEnclosure().getUrl() : null;
		if (imageUrl != null) {
			try {
				enclosure = new URL(imageUrl.trim());
			}
			catch (final MalformedURLException mue) {
				logger.log(Level.WARNING, Messages.get("err.url.malformed", imageUrl), mue);
			}
		}

		return new Earthquake(rssItem.getGuid().getContent().trim(), time, magnitudo, new Latitude(latitude), new Longitude(longitude), new Depth(depth), status, region, link, enclosure);
	}

}
