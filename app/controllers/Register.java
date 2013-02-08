package controllers;

import models.CellPhone;
import models.EmailToUserDbo;
import models.UserDbo;

import com.alvazan.play.NoSql;

import controllers.auth.Secure;
import controllers.auth.Secure.Security;
import play.mvc.Controller;

public class Register extends Controller {

	public static void postRegister(String email, String password, String verifyPassword) throws Throwable {
		validation.required(email);
		if(password == null) {
			validation.addError("password", "password must be supplied");
		} else if(!password.equals(verifyPassword)) {
			validation.addError("verifyPassword", "Passwords do not match");
		} else if(!email.contains("@"))
			validation.addError("email", "This is not a valid email");
		
		EmailToUserDbo existing = NoSql.em().find(EmailToUserDbo.class, email);
		if(existing != null) {
			validation.addError("email", "This email already exists");
		}
		
		if(validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
	        validation.keep(); // keep the errors for the next request
			Secure.login();
		}

		
		UserDbo user = new UserDbo();
		user.setEmail(email);
		user.setPassword(password);
		NoSql.em().put(user);
		
		EmailToUserDbo emailToUser = new EmailToUserDbo();
		emailToUser.setId(email);
		emailToUser.setValue(user.getId());
		NoSql.em().put(emailToUser);
		
		String key = session.get("key");
		if(key != null) {
			session.remove("key");
			CellPhone phone = NoSql.em().find(CellPhone.class, key);
			if(phone == null) {
				NoSql.em().flush();
				validation.addError("key", "Key was invalid");
				Application.setup();
			}
			
			user.addPhone(phone);
			NoSql.em().put(user);
			NoSql.em().put(phone);
		}

		NoSql.em().flush();
		
		Secure.addUserToSession(user.getEmail());
		
		MyStuff.cellPhones();
	}
}
