package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlOneToMany;

@NoSqlEntity
public class CellPhone {

	@NoSqlIndexed
	@NoSqlId(usegenerator=false)
	private String key;

	@NoSqlOneToMany
	private List<UserDbo> users = new ArrayList<UserDbo>();

	@NoSqlOneToMany
	private List<TimePeriodDbo> periods = new ArrayList<TimePeriodDbo>();

	private transient Map<DateTime, TimePeriodDbo> timePeriods;
	private String phoneNumber;
	private int messageCount;
	private boolean paid;
	private String transactionId;
	private String name;
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public List<UserDbo> getUsers() {
		return users;
	}

	public void setUsers(List<UserDbo> users) {
		this.users = users;
	}

	public List<TimePeriodDbo> getPeriods() {
		return periods;
	}

	public void setPeriods(List<TimePeriodDbo> messages) {
		this.periods = messages;
	}

	public TimePeriodDbo getPeriod(DateTime time) {
		if(timePeriods == null) {
			timePeriods = new HashMap<DateTime, TimePeriodDbo>();
			for(TimePeriodDbo period : periods) {
				DateTime key = period.getBeginOfMonth();
				timePeriods.put(key, period);
			}
		}
		return timePeriods.get(time);
	}

	protected void addUser(UserDbo userDbo) {
		this.users.add(userDbo);
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void addPeriod(TimePeriodDbo period) {
		periods.add(period);
		period.setPhone(this);
	}

	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int count) {
		this.messageCount = count;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setCreditCardTxId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getCreditCardTxId() {
		return transactionId;
	}
}
