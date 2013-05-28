package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlIndexed;
import com.alvazan.orm.api.base.anno.NoSqlOneToMany;

@NoSqlEntity
public class UserDbo {

	@NoSqlId
	private String id;
	
	@NoSqlIndexed
	private String email;
	
	private String password;
	
	@NoSqlOneToMany
	private List<CellPhone> phones = new ArrayList<CellPhone>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<CellPhone> getPhones() {
		return phones;
	}

	public void addPhone(CellPhone c) {
		this.phones.add(c);
		c.addUser(this);
	}

	
}
