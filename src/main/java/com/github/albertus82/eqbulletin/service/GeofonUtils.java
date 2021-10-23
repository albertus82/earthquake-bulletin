package com.github.albertus82.eqbulletin.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import com.github.albertus82.eqbulletin.config.EarthquakeBulletinConfig;
import com.github.albertus82.eqbulletin.gui.preference.Preference;
import com.github.albertus82.eqbulletin.service.net.ConnectionUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeofonUtils {

	public static final String OLD_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de/old";
	public static final String NEW_GEOFON_BASE_URL = "https://geofon.gfz-potsdam.de";

	public static final String DEFAULT_GEOFON_BASE_URL = OLD_GEOFON_BASE_URL;

	public static final String MOMENT_TENSOR_FILENAME = "mt.txt";
	private static final String BEACH_BALL_FILENAME = "bb.png";

	public static URI getEventMapUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return ConnectionUtils.toURI(getEventBaseUrl(guid, year) + guid + ".jpg");
	}

	public static URI getEventMomentTensorUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return ConnectionUtils.toURI(getEventBaseUrl(guid, year) + MOMENT_TENSOR_FILENAME);
	}

	public static URI getBeachBallUri(@NonNull final String guid, final int year) throws MalformedURLException, URISyntaxException {
		return ConnectionUtils.toURI(getEventBaseUrl(guid, year) + BEACH_BALL_FILENAME);
	}

	public static String getBulletinBaseUrl() throws MalformedURLException {
		return getBaseUrl() + "/eqinfo/list.php";
	}

	private static String getEventBaseUrl(@NonNull final String guid, final int year) throws MalformedURLException {
		return getBaseUrl() + "/data/alerts/" + year + "/" + guid + "/";
	}

	private static String getBaseUrl() throws MalformedURLException {
		final String spec = EarthquakeBulletinConfig.getPreferencesConfiguration().getString(Preference.GEOFON_BASE_URL, DEFAULT_GEOFON_BASE_URL);
		return ConnectionUtils.sanitizeUriString(spec);
	}

}
