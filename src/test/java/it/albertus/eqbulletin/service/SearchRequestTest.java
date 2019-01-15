package it.albertus.eqbulletin.service;

import java.net.MalformedURLException;

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

	public void testGenerateUrl() throws MalformedURLException {
		final String a = GeofonUtils.getBaseUrl();
		final String b = "/eqinfo/list.php?";

		final SearchRequest r = new SearchRequest();
		Assert.assertEquals(a + b + "fmt=html", r.toURL());

		r.getParameterMap().put("fmt", "rss");
		Assert.assertEquals(a + b + "fmt=rss", r.toURL());

		r.getParameterMap().put("fmt", "html");
		Assert.assertEquals(a + b + "fmt=html", r.toURL());

	}
}
