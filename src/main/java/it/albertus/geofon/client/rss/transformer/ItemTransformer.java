package it.albertus.geofon.client.rss.transformer;

import it.albertus.geofon.client.model.Earthquake;
import it.albertus.geofon.client.model.Status;
import it.albertus.geofon.client.rss.xml.Item;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/* Item [guid=Guid [permaLink=false],
 *       title=M 4.3, Central Italy, 
 *       enclosure=Enclosure [length=52656, type=image/jpeg, url=http://geofon.gfz-potsdam.de//data/alerts/2016/gfz2016rige/gfz2016rige.jpg],
 *       description=2016-09-03 10:18:54  42.94   13.23    10 km    C,
 *       link=http://geofon.gfz-potsdam.de/eqinfo/event.php?from=rss&id=gfz2016rige] */
public class ItemTransformer {

	private static final DateFormat rssDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		rssDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static Earthquake fromRss(final Item rssItem) {
		// Title
		final String title = rssItem.getTitle();
		final float magnitudo = Float.parseFloat(title.substring(1, title.indexOf(',')).trim());
		final String region = title.substring(title.indexOf(',') + 1).trim();

		// Description
		final String[] descriptionTokens = rssItem.getDescription().split("\\s+");
		final Date time = parseRssDate(descriptionTokens[0].trim() + ' ' + descriptionTokens[1].trim());
		final float latitude = Float.parseFloat(descriptionTokens[2].trim());
		final float longitude = Float.parseFloat(descriptionTokens[3].trim());
		final int depth = Integer.parseInt(descriptionTokens[4].trim());
		final Status status = Status.valueOf(descriptionTokens[6].trim());

		// Links
		URL link = null;
		final String pageUrl = rssItem.getLink();
		if (pageUrl != null) {
			try {
				link = new URL(pageUrl.trim());
			}
			catch (final MalformedURLException mue) {
				mue.printStackTrace();
			}
		}

		URL enclosure = null;
		final String imageUrl = rssItem.getEnclosure() != null ? rssItem.getEnclosure().getUrl() : null;
		if (imageUrl != null) {
			try {
				enclosure = new URL(imageUrl.trim());
			}
			catch (final MalformedURLException mue) {
				mue.printStackTrace();
			}
		}

		return new Earthquake(rssItem.getGuid().getContent().trim(), time, magnitudo, latitude, longitude, depth, status, region, link, enclosure);
	}

	private static synchronized Date parseRssDate(final String source) {
		try {
			return rssDateFormat.parse(source);
		}
		catch (final ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}

}
