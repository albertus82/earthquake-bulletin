package com.github.albertus82.eqbulletin.service.decode.html;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.jsoup.nodes.Element;

import com.github.albertus82.eqbulletin.model.Earthquake;

import lombok.NonNull;

public abstract class AbstractHtmlBulletinDecoder implements HtmlBulletinDecoder {

	protected static final String DEGREE_SIGN = "\u00B0";

	protected static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").parseDefaulting(ChronoField.MILLI_OF_SECOND, 0).toFormatter().withZone(ZoneOffset.UTC);

	protected abstract Earthquake decodeItem(@NonNull final Element element);

}
