package it.albertus.eqbulletin.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import it.albertus.eqbulletin.model.Format;

public class SearchRequestTest {

	@Test
	public void testGetFormat() {
		final SearchRequest r = new SearchRequest();
		Assert.assertEquals(Format.DEFAULT, r.getFormat());

		r.getParameterMap().put("fmt", "rss");
		Assert.assertEquals(Format.RSS, r.getFormat());
		Assert.assertEquals("rss", r.getFormat().getValue());

		r.getParameterMap().put("fmt", "html");
		Assert.assertEquals(Format.HTML, r.getFormat());
		Assert.assertEquals("html", r.getFormat().getValue());

		r.getParameterMap().put("fmt", "");
		Assert.assertEquals(Format.DEFAULT, r.getFormat());

		r.getParameterMap().put("fmt", "qwerty");
		Assert.assertEquals(Format.DEFAULT, r.getFormat());
	}

	public void testGenerateUrl() throws MalformedURLException, URISyntaxException {
		final String baseUrl = GeofonUtils.getBulletinBaseUrl();

		final SearchRequest r = new SearchRequest();
		Assert.assertEquals(baseUrl + "fmt=html", r.toURI().toURL());

		r.getParameterMap().put("fmt", "rss");
		Assert.assertEquals(baseUrl + "fmt=rss", r.toURI().toURL());

		r.getParameterMap().put("fmt", "html");
		Assert.assertEquals(baseUrl + "fmt=html", r.toURI().toURL());
	}
}
