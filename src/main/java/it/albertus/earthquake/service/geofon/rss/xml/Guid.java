package it.albertus.earthquake.service.geofon.rss.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Guid {

	private String content;

	private boolean permaLink;

	@XmlValue
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@XmlAttribute
	public boolean isPermaLink() {
		return permaLink;
	}

	public void setPermaLink(boolean permaLink) {
		this.permaLink = permaLink;
	}

	@Override
	public String toString() {
		return "Guid [" + (content != null ? "content=" + content + ", " : "") + "permaLink=" + permaLink + "]";
	}

}
