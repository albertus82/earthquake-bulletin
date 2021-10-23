package io.github.albertus82.eqbulletin.service.decode.html;

import java.util.List;

import org.jsoup.nodes.Document;

import io.github.albertus82.eqbulletin.model.Earthquake;

public interface HtmlBulletinDecoder {

	List<Earthquake> decode(Document document);

}
