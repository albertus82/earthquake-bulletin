package it.albertus.eqbulletin.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.dmurph.URIEncoder;

import it.albertus.eqbulletin.model.Format;

public class SearchRequest {

	private boolean valid;
	private long delay = -1;
	private final Map<String, String> parameterMap = new LinkedHashMap<>();

	public boolean isValid() {
		return valid;
	}

	public void setValid(final boolean valid) {
		this.valid = valid;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(final long delay) {
		this.delay = delay;
	}

	public Format getFormat() {
		return Format.forValue(parameterMap.get(Format.KEY));
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}

	public URI toURI() throws URISyntaxException {
		return new URI(toUrlString());
	}

	private String toUrlString() {
		final StringBuilder url = new StringBuilder(GeofonUtils.getBulletinBaseUrl()).append('?').append(Format.KEY).append('=').append(getFormat().getValue());
		for (final Entry<String, String> param : parameterMap.entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !Format.KEY.equals(param.getKey())) {
				url.append('&').append(param.getKey()).append('=').append(URIEncoder.encodeURI(param.getValue()));
			}
		}
		return url.toString();
	}

	@Override
	public String toString() {
		return "SearchRequest [valid=" + valid + ", delay=" + delay + ", parameterMap=" + parameterMap + "]";
	}

}
