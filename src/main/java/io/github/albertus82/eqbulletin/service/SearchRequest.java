package io.github.albertus82.eqbulletin.service;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.dmurph.URIEncoder;

import io.github.albertus82.eqbulletin.model.Format;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class SearchRequest {

	private boolean valid;
	private Long delay;
	private Short limit;
	private final Map<String, String> parameterMap = new LinkedHashMap<>();

	public Optional<Long> getDelay() {
		return Optional.ofNullable(delay);
	}

	public Optional<Short> getLimit() {
		return Optional.ofNullable(limit);
	}

	public Format getFormat() {
		return Format.forValue(parameterMap.get(Format.KEY));
	}

	public List<URI> toURIs() throws URISyntaxException, MalformedURLException {
		final List<URI> uris = new ArrayList<>();
		for (final String url : toUrlStrings()) {
			uris.add(new URI(url));
		}
		return uris;
	}

	private Set<String> toUrlStrings() throws MalformedURLException {
		final StringBuilder baseUrl = new StringBuilder(GeofonUtils.getBulletinBaseUrl());
		baseUrl.append('?').append(Format.KEY).append('=').append(getFormat().getValue());
		for (final Entry<String, String> param : parameterMap.entrySet()) {
			if (param.getValue() != null && !param.getValue().isEmpty() && !Format.KEY.equals(param.getKey())) {
				baseUrl.append('&').append(param.getKey()).append('=').append(URIEncoder.encodeURI(param.getValue()));
			}
		}
		if (limit != null) {
			final PaginationParameters pp = new PaginationParameters(limit);
			if (pp.getPages() == 1) {
				return Collections.singleton(baseUrl.append("&nmax=").append(pp.getNmax()).toString());
			}
			else {
				final Set<String> urls = new LinkedHashSet<>();
				for (short page = 1; page <= pp.getPages(); page++) {
					urls.add(baseUrl + "&page=" + page + "&nmax=" + pp.getNmax()); // Don't append to the StringBuilder here!
				}
				return urls;
			}
		}
		else {
			return Collections.singleton(baseUrl.toString());
		}
	}

	@Override
	public String toString() {
		return "SearchRequest [valid=" + valid + ", delay=" + delay + ", limit=" + limit + ", parameterMap=" + parameterMap + "]";
	}

	@Getter(AccessLevel.PACKAGE)
	static class PaginationParameters implements Serializable {

		private static final long serialVersionUID = 8859166869888375735L;

		private static final short API_LIMIT = 1000;

		private short nmax;
		private byte pages;

		PaginationParameters(final short limit) {
			nmax = limit;
			pages = 1;
			log.debug("Desired (nmax={}).", limit);
			while (nmax > API_LIMIT) {
				pages++;
				nmax = (short) Math.ceil((double) limit / pages);
				log.debug("Computing (nmax={}, pages={})...", nmax, pages);
			}
			log.debug("Computed (nmax={}, pages={}).", nmax, pages);
		}

	}

}
