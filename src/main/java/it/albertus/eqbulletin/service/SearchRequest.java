package it.albertus.eqbulletin.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dmurph.URIEncoder;

import it.albertus.eqbulletin.model.Format;

public class SearchRequest {

	private boolean formValid;
	private long waitTimeInMillis;
	private final Map<String, String> parameterMap = new LinkedHashMap<>();

	public boolean isFormValid() {
		return formValid;
	}

	public void setFormValid(final boolean formValid) {
		this.formValid = formValid;
	}

	public long getWaitTimeInMillis() {
		return waitTimeInMillis;
	}

	public void setWaitTimeInMillis(final long waitTimeInMillis) {
		this.waitTimeInMillis = waitTimeInMillis;
	}

	public Format getFormat() {
		return Format.forValue(parameterMap.get(Format.KEY));
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public URL toURL() throws MalformedURLException {
		final StringBuilder url = new StringBuilder(GeofonUtils.getBaseUrl()).append("/eqinfo/list.php?").append(Format.KEY).append('=').append(getFormat().getValue());
		for (final Entry<String, String> param : parameterMap.entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !Format.KEY.equals(param.getKey())) {
				url.append('&').append(param.getKey()).append('=').append(URIEncoder.encodeURI(param.getValue()));
			}
		}
		return new URL(url.toString());
	}

}
