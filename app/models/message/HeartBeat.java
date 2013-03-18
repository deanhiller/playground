package models.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This is for heartbeat
 * 
 * @author easility
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class HeartBeat {

	@JsonProperty("key")
	@XmlElement(name = "key")
	public String key;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}