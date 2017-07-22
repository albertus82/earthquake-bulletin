package it.albertus.earthquake.service;

import java.util.LinkedHashMap;
import java.util.Map;

import it.albertus.earthquake.model.Format;

public class SearchJobVars {

	private boolean formValid;
	private long waitTimeInMillis;
	private Format format;
	private boolean error;
	private final Map<String, String> params = new LinkedHashMap<>();

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

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Map<String, String> getParams() {
		return params;
	}

}
