package it.albertus.eqbulletin.service.rss.xml;

import javax.xml.bind.annotation.XmlAttribute;

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
		return "Enclosure [" + (length != null ? "length=" + length + ", " : "") + (type != null ? "type=" + type + ", " : "") + (url != null ? "url=" + url : "") + "]";
	}

}
