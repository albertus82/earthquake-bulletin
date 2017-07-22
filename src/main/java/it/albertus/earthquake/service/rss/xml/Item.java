package it.albertus.earthquake.service.rss.xml;

public class Item {

	private Guid guid;

	private String title;

	private Enclosure enclosure;

	private String description;

	private String link;

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

	@Override
	public String toString() {
		return "Item [" + (guid != null ? "guid=" + guid + ", " : "") + (title != null ? "title=" + title + ", " : "") + (enclosure != null ? "enclosure=" + enclosure + ", " : "") + (description != null ? "description=" + description + ", " : "") + (link != null ? "link=" + link : "") + "]";
	}

}
