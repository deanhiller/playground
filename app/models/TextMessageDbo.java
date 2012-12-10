package models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlManyToOne;

@NoSqlEntity
public class TextMessageDbo {

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
}
