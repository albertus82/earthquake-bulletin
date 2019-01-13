package it.albertus.eqbulletin.service.decode.rss.xml;

import java.util.Arrays;

public class Channel {

	private String title;

	private String description;

	private String link;

	private Item[] item;

	private String ttl;

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

	public Item[] getItem() {
		return item;
	}

	public void setItem(Item[] item) {
		this.item = item;
	}

	public String getTtl() {
		return ttl;
	}

	public void setTtl(String ttl) {
		this.ttl = ttl;
	}

	@Override
	public String toString() {
		return "Channel [" + (title != null ? "title=" + title + ", " : "") + (description != null ? "description=" + description + ", " : "") + (link != null ? "link=" + link + ", " : "") + (item != null ? "item=" + Arrays.toString(item) + ", " : "") + (ttl != null ? "ttl=" + ttl : "") + "]";
	}

}
