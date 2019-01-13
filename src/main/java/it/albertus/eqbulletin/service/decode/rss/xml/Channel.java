package it.albertus.eqbulletin.service.decode.rss.xml;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;

public class Channel {

	private String title;
	private String description;
	private String link;
	private Item[] items;
	private Integer ttl;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	@XmlElement(name = "item")
	public Item[] getItems() {
		return items;
	}

	public void setItems(Item[] items) {
		this.items = items;
	}

	public Integer getTtl() {
		return ttl;
	}

	public void setTtl(Integer ttl) {
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		return "Channel [title=" + title + ", description=" + description + ", link=" + link + ", item=" + Arrays.toString(items) + ", ttl=" + ttl + "]";
	}

}
