package it.albertus.geofon.client.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ItemDescription {

	private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	static {
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private final Date time;
	private final float latitude;
	private final float longitude;
	private final int depth;
	private final Status status;

	public ItemDescription(final String description) {
		String[] tokens = description.split("\\s+");
		time = parseDate(tokens[0].trim() + ' ' + tokens[1].trim());
		latitude = Float.parseFloat(tokens[2].trim());
		longitude = Float.parseFloat(tokens[3].trim());
		depth = Integer.parseInt(tokens[4].trim());
		status = Status.valueOf(tokens[6].trim());
	}

	public Date getTime() {
		return time;
	}

	public float getLatitude() {
		return latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public int getDepth() {
		return depth;
	}

	public Status getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "ItemDescription [time=" + time + ", latitude=" + latitude + ", longitude=" + longitude + ", depth=" + depth + ", status=" + status + "]";
	}

	private static synchronized Date parseDate(final String source) {
		try {
			return dateFormat.parse(source);
		}
		catch (final ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}

}
