package it.albertus.eqbulletin.service.decode.html;

import org.jsoup.nodes.Element;

import it.albertus.eqbulletin.model.Earthquake;
import lombok.NonNull;

public abstract class AbstractHtmlBulletinDecoder implements HtmlBulletinDecoder {

	protected abstract Earthquake decodeItem(@NonNull final Element element);

}
