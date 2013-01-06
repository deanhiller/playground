package models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlManyToOne;
import com.alvazan.orm.api.base.anno.NoSqlOneToMany;

@NoSqlEntity
public class TimePeriodDbo {

	private static DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM dd, yyyy");
	
	@NoSqlId(usegenerator=false)
	private String id;
	
	private DateTime beginOfMonth;
	
	private long beginOfMonthLong;
	
	@NoSqlManyToOne
	private CellPhone phone;
	
	@NoSqlOneToMany
	private List<TextMessageDbo> messages = new ArrayList<TextMessageDbo>();

	public String getId() {
		return id;
	}

	public void setId(String number, DateTime beginOfMonth) {
		this.beginOfMonth = beginOfMonth;
		this.beginOfMonthLong = beginOfMonth.getMillis();
		this.id = formKey(number, beginOfMonthLong);
	}

	public String getRange() {
		DateTime end = beginOfMonth.plusMonths(1);
		end = end.minusDays(1);
		return fmt.print(beginOfMonth) +" to "+ fmt.print(end);
	}
	
	public DateTime getBeginOfMonth() {
		return beginOfMonth;
	}

	public List<TextMessageDbo> getMessages() {
		return messages;
	}

	public void addMessage(TextMessageDbo msg) {
		messages.add(msg);
		msg.setTimePeriod(this);
	}

	public CellPhone getPhone() {
		return phone;
	}

	public void setPhone(CellPhone phone) {
		this.phone = phone;
	}

	public long getBeginOfMonthLong() {
		return beginOfMonthLong;
	}

	public void setBeginOfMonthLong(long beginOfMonthLong) {
		this.beginOfMonthLong = beginOfMonthLong;
	}

	public static String formKey(String number, long period) {
		return number+"-"+period;
	}
	
}
