package models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlOneToMany;

@NoSqlEntity
public class TimePeriodDbo {

	@NoSqlId
	private String id;
	
	private DateTime beginOfMonth;
	
	@NoSqlIndexed
	private long beginOfMonthLong;
	
	@NoSqlOneToMany
	private List<TextMessageDbo> messages = new ArrayList<TextMessageDbo>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DateTime getBeginOfMonth() {
		return beginOfMonth;
	}

	public void setBeginOfMonth(DateTime beginOfMonth) {
		this.beginOfMonth = beginOfMonth;
		this.beginOfMonthLong = beginOfMonth.getMillis();
	}

	public List<TextMessageDbo> getMessages() {
		return messages;
	}

	public void addMessage(TextMessageDbo msg) {
		messages.add(msg);
		msg.setTimePeriod(this);
	}
}
