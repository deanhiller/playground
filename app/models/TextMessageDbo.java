package models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlManyToOne;

@NoSqlEntity
public class TextMessageDbo {

	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy hh:mm a");
	
	@NoSqlId
	private String id;
	@NoSqlManyToOne
	private TimePeriodDbo timePeriod;
	private boolean isToCell;
	private DateTime timeReceived;
	private long cellTimeReceived;
	private String cellNumber;
	private String remoteNumber;
	private String textMessage;
	private boolean displayedForFree;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public int getSize() {
		return textMessage.length();
	}
	public String getTextMessage() {
		return textMessage;
	}

	public void setTextMessage(String textMessage) {
		this.textMessage = textMessage;
	}

	public DateTime getTimeReceived() {
		return timeReceived;
	}

	public void setTimeReceived(DateTime timeReceived2) {
		this.timeReceived = timeReceived2;
	}

	public long getCellTimeReceived() {
		return cellTimeReceived;
	}

	public void setCellTimeReceived(long cellTimeReceived) {
		this.cellTimeReceived = cellTimeReceived;
	}

	public TimePeriodDbo getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriodDbo timePeriods) {
		this.timePeriod = timePeriods;
	}

	public boolean isToCell() {
		return isToCell;
	}

	public void setToCell(boolean isToCell) {
		this.isToCell = isToCell;
	}
	
	public String getDirection() {
		if(isToCell) 
			return "Received";
		return "Sent";
	}
	
	public String getTimeString() {
		Instant instant = new Instant(this.cellTimeReceived);
		DateTime time = instant.toDateTime();
		return fmt.print(time);
	}

	public void setDisplayedForFree(boolean displayed) {
		this.displayedForFree = displayed;
	}
	public boolean isDisplayedForFree() {
		return displayedForFree;
	}
	
}
