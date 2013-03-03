package models.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * This a PUT message
 * @author mcottere
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PutTextMessage {

	@JsonProperty("key")
	@XmlElement(name="key")
    public String key;

	@JsonProperty("cellNumber")
	@XmlElement(name="cellNumber")
    public String cellNumber;
	
	@JsonProperty("remoteNumber")
	@XmlElement(name="remoteNumber")
    public String remoteNumber;

	@JsonProperty("outgoing")
	@XmlElement(name="outgoing")
    public boolean isOutgoing;
	
	@JsonProperty("phoneTime")
	@XmlElement(name="phoneTime")
    public long phoneTime;
	
	@JsonProperty("textMessage")
	@XmlElement(name="textMessage")
    public String textMessage;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public long getPhoneTime() {
		return phoneTime;
	}

	public void setPhoneTime(long phoneTime) {
		this.phoneTime = phoneTime;
	}

	public String getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(String cellNumber) {
		this.cellNumber = cellNumber;
	}

	public String getRemoteNumber() {
		return remoteNumber;
	}

	public void setRemoteNumber(String remoteNumber) {
		this.remoteNumber = remoteNumber;
	}

	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	public boolean isOutgoing() {
		return isOutgoing;
	}

	public void setOutgoing(boolean isOutgoing) {
		this.isOutgoing = isOutgoing;
	}
	
} // PutMessage
