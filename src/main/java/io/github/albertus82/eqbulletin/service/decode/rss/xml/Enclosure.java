package io.github.albertus82.eqbulletin.service.decode.rss.xml;

import jakarta.xml.bind.annotation.XmlAttribute;

public class Enclosure {

	private String length;
	private String type;
	private String url;

	@XmlAttribute
	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	@XmlAttribute
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlAttribute
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Enclosure [length=" + length + ", type=" + type + ", url=" + url + "]";
	}

}
