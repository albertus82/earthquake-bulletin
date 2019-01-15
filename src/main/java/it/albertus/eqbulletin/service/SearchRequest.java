package it.albertus.eqbulletin.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dmurph.URIEncoder;

import it.albertus.eqbulletin.config.EarthquakeBulletinConfig;
import it.albertus.eqbulletin.gui.SearchForm;
import it.albertus.eqbulletin.model.Format;
import it.albertus.jface.preference.PreferencesConfiguration;

public class SearchRequest {

	private static final PreferencesConfiguration configuration = EarthquakeBulletinConfig.getInstance();

	private boolean formValid;
	private long waitTimeInMillis;
	private Format format = SearchForm.Defaults.FORMAT;
	private final Map<String, String> parameterMap = new LinkedHashMap<>();

	public boolean isFormValid() {
		return formValid;
	}

	public void setFormValid(boolean formValid) {
		this.formValid = formValid;
	}

	public long getWaitTimeInMillis() {
		return waitTimeInMillis;
	}

	public void setWaitTimeInMillis(long waitTimeInMillis) {
		this.waitTimeInMillis = waitTimeInMillis;
	}

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public URL generateUrl() throws MalformedURLException {
		final StringBuilder url = new StringBuilder(configuration.getString("url.base", GeofonUtils.DEFAULT_BASE_URL)).append("/eqinfo/list.php?fmt=").append(parameterMap.get("fmt"));
		for (final Entry<String, String> param : parameterMap.entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !"fmt".equals(param.getKey())) {
				url.append('&').append(param.getKey()).append('=').append(URIEncoder.encodeURI(param.getValue()));
			}
		}
		return new URL(url.toString());
	}

}
