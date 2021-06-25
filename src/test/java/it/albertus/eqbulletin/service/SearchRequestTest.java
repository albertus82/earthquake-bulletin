package it.albertus.eqbulletin.service;

import java.net.URISyntaxException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import it.albertus.eqbulletin.model.Format;
import it.albertus.eqbulletin.service.SearchRequest.PaginationParameters;

class SearchRequestTest {

	@Test
	void testGetFormat() {
		final SearchRequest r = new SearchRequest();
		Assertions.assertEquals(Format.DEFAULT, r.getFormat());

		r.getParameterMap().put("fmt", "rss");
		Assertions.assertEquals(Format.RSS, r.getFormat());
		Assertions.assertEquals("rss", r.getFormat().getValue());

		r.getParameterMap().put("fmt", "html");
		Assertions.assertEquals(Format.HTML, r.getFormat());
		Assertions.assertEquals("html", r.getFormat().getValue());

		r.getParameterMap().put("fmt", "");
		Assertions.assertEquals(Format.DEFAULT, r.getFormat());

		r.getParameterMap().put("fmt", "qwerty");
		Assertions.assertEquals(Format.DEFAULT, r.getFormat());
	}

	@Test
	void testGenerateUrl() throws URISyntaxException {
		final String baseUrl = GeofonUtils.getBulletinBaseUrl();

		final SearchRequest r = new SearchRequest();
		Assertions.assertEquals(baseUrl + "?fmt=html", r.toURIs().get(0).toString());

		r.getParameterMap().put("fmt", "rss");
		Assertions.assertEquals(baseUrl + "?fmt=rss", r.toURIs().get(0).toString());

		r.getParameterMap().put("fmt", "html");
		Assertions.assertEquals(baseUrl + "?fmt=html", r.toURIs().get(0).toString());
	}

	@Test
	void testPaginationParameters() {
		PaginationParameters pp = new PaginationParameters((short) 1);
		Assertions.assertEquals(1, pp.getPages());
		Assertions.assertEquals(1, pp.getNmax());

		pp = new PaginationParameters((short) 500);
		Assertions.assertEquals(1, pp.getPages());
		Assertions.assertEquals(500, pp.getNmax());

		pp = new PaginationParameters((short) 999);
		Assertions.assertEquals(1, pp.getPages());
		Assertions.assertEquals(999, pp.getNmax());

		pp = new PaginationParameters((short) 1000);
		Assertions.assertEquals(1, pp.getPages());
		Assertions.assertEquals(1000, pp.getNmax());

		pp = new PaginationParameters((short) 1001);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(501, pp.getNmax());

		pp = new PaginationParameters((short) 1002);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(501, pp.getNmax());

		pp = new PaginationParameters((short) 1003);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(502, pp.getNmax());

		pp = new PaginationParameters((short) 1004);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(502, pp.getNmax());

		pp = new PaginationParameters((short) 1005);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(503, pp.getNmax());

		pp = new PaginationParameters((short) 1998);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(999, pp.getNmax());

		pp = new PaginationParameters((short) 1999);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(1000, pp.getNmax());

		pp = new PaginationParameters((short) 2000);
		Assertions.assertEquals(2, pp.getPages());
		Assertions.assertEquals(1000, pp.getNmax());

		pp = new PaginationParameters((short) 2001);
		Assertions.assertEquals(3, pp.getPages());
		Assertions.assertEquals(667, pp.getNmax());

		pp = new PaginationParameters((short) 2002);
		Assertions.assertEquals(3, pp.getPages());
		Assertions.assertEquals(668, pp.getNmax());

		pp = new PaginationParameters((short) 2003);
		Assertions.assertEquals(3, pp.getPages());
		Assertions.assertEquals(668, pp.getNmax());

		pp = new PaginationParameters((short) 2004);
		Assertions.assertEquals(3, pp.getPages());
		Assertions.assertEquals(668, pp.getNmax());

		pp = new PaginationParameters((short) 2005);
		Assertions.assertEquals(3, pp.getPages());
		Assertions.assertEquals(669, pp.getNmax());
	}
}
