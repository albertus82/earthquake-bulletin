package it.albertus.eqbulletin.service.decode.html;

import java.util.List;

import org.jsoup.nodes.Document;

import it.albertus.eqbulletin.model.Earthquake;

public interface HtmlBulletinDecoder {

	List<Earthquake> decode(Document document);

}
