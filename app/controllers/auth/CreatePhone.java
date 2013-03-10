package controllers.auth;

import play.data.validation.Validation;
import play.mvc.Scope.Session;
import models.CellPhone;
import models.EmailToUserDbo;
import models.UserDbo;

import com.alvazan.play.NoSql;

import controllers.Application;

public class CreatePhone {

	public static void setupKeyPhone(String username) {
		EmailToUserDbo emailXref = NoSql.em().find(EmailToUserDbo.class, username);
		String userId = emailXref.getValue();
		UserDbo user = NoSql.em().find(UserDbo.class, userId);

		Session session = Session.current();
		String key = session.get("key");
		if(key != null) {
			session.remove("key");
			CellPhone phone = NoSql.em().find(CellPhone.class, key);
			if(phone == null) {
				NoSql.em().flush();
				Validation.current().addError("key", "Key was invalid");
				Application.setup();
			}
			
			user.addPhone(phone);
			NoSql.em().put(user);
			NoSql.em().put(phone);
			NoSql.em().flush();
		}
	}

}
