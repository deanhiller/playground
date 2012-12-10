package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.OneToMany;

import com.alvazan.orm.api.base.anno.NoSqlEntity;
import com.alvazan.orm.api.base.anno.NoSqlId;
import com.alvazan.orm.api.base.anno.NoSqlOneToMany;

@NoSqlEntity
public class UserDbo {

	@NoSqlId
	private String id;
	
	private String email;
	
	private String password;
	
	@NoSqlOneToMany
	private List<CellPhone> keys = new ArrayList<CellPhone>();

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

	public List<CellPhone> getKeys() {
		return keys;
	}

	public void setKeys(List<CellPhone> keys) {
		this.keys = keys;
	}
	
}
