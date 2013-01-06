package controllers;

import com.alvazan.play.NoSql;

import play.mvc.Scope.Session;
import play.mvc.results.Unauthorized;
import models.EmailToUserDbo;
import models.UserDbo;

public class Utility {

	public static UserDbo fetchUser() {
		String userName = Session.current().get("username");
		EmailToUserDbo ref = NoSql.em().find(EmailToUserDbo.class, userName);
		if(ref == null) {
			throw new Unauthorized("bug, user not found, corrupt session");
		}
		
		UserDbo user = NoSql.em().find(UserDbo.class, ref.getValue());
		return user;
	}

}
