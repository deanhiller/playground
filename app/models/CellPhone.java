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
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
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

}
