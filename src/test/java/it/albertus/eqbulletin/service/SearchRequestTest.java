package it.albertus.eqbulletin.service;

import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.service.SearchRequest.PaginationParameters;

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

	@Test
	public void testGenerateUrl() throws URISyntaxException {
		final String baseUrl = GeofonUtils.getBulletinBaseUrl();

		final SearchRequest r = new SearchRequest();
		Assert.assertEquals(baseUrl + "?fmt=html", r.toURIs().get(0).toString());

		r.getParameterMap().put("fmt", "rss");
		Assert.assertEquals(baseUrl + "?fmt=rss", r.toURIs().get(0).toString());

		r.getParameterMap().put("fmt", "html");
		Assert.assertEquals(baseUrl + "?fmt=html", r.toURIs().get(0).toString());
	}

	@Test
	public void testPaginationParameters() {
		PaginationParameters pp = new PaginationParameters((short) 1);
		Assert.assertEquals(1, pp.getPages());
		Assert.assertEquals(1, pp.getNmax());

		pp = new PaginationParameters((short) 500);
		Assert.assertEquals(1, pp.getPages());
		Assert.assertEquals(500, pp.getNmax());

		pp = new PaginationParameters((short) 999);
		Assert.assertEquals(1, pp.getPages());
		Assert.assertEquals(999, pp.getNmax());

		pp = new PaginationParameters((short) 1000);
		Assert.assertEquals(1, pp.getPages());
		Assert.assertEquals(1000, pp.getNmax());

		pp = new PaginationParameters((short) 1001);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(501, pp.getNmax());

		pp = new PaginationParameters((short) 1002);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(501, pp.getNmax());

		pp = new PaginationParameters((short) 1003);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(502, pp.getNmax());

		pp = new PaginationParameters((short) 1004);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(502, pp.getNmax());

		pp = new PaginationParameters((short) 1005);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(503, pp.getNmax());

		pp = new PaginationParameters((short) 1998);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(999, pp.getNmax());

		pp = new PaginationParameters((short) 1999);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(1000, pp.getNmax());

		pp = new PaginationParameters((short) 2000);
		Assert.assertEquals(2, pp.getPages());
		Assert.assertEquals(1000, pp.getNmax());

		pp = new PaginationParameters((short) 2001);
		Assert.assertEquals(3, pp.getPages());
		Assert.assertEquals(667, pp.getNmax());

		pp = new PaginationParameters((short) 2002);
		Assert.assertEquals(3, pp.getPages());
		Assert.assertEquals(668, pp.getNmax());

		pp = new PaginationParameters((short) 2003);
		Assert.assertEquals(3, pp.getPages());
		Assert.assertEquals(668, pp.getNmax());

		pp = new PaginationParameters((short) 2004);
		Assert.assertEquals(3, pp.getPages());
		Assert.assertEquals(668, pp.getNmax());

		pp = new PaginationParameters((short) 2005);
		Assert.assertEquals(3, pp.getPages());
		Assert.assertEquals(669, pp.getNmax());
	}
}
