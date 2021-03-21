package it.albertus.eqbulletin.service.decode.rss.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rss")
public class RssBulletin {

	private Channel channel;
	private String version;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	@XmlAttribute
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "RssBulletin [channel=" + channel + ", version=" + version + "]";
	}

}
