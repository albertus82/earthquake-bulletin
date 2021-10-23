package io.github.albertus82.eqbulletin.service.decode.rss.xml;

import jakarta.xml.bind.annotation.XmlElement;

public class Item {

	private Guid guid;
	private String title;
	private Enclosure enclosure;
	private String description;
	private String link;
	private String mt;

	public Guid getGuid() {
		return guid;
	}

	public void setGuid(Guid guid) {
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Enclosure getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(Enclosure enclosure) {
		this.enclosure = enclosure;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@XmlElement(name = "geofon_mt")
	public String getMt() {
		return mt;
	}

	public void setMt(String mt) {
		this.mt = mt;
	}

	@Override
	public String toString() {
		return "Item [guid=" + guid + ", title=" + title + ", enclosure=" + enclosure + ", description=" + description + ", link=" + link + ", mt=" + mt + "]";
	}

}
