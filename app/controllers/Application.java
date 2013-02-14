package controllers;

import models.CellPhone;
import models.EmailToUserDbo;
import models.UserDbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.play.NoSql;

import controllers.auth.Secure;

import play.mvc.Controller;

public class Application extends Controller {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static boolean isLoggedIn() {
    	String username = session.get("username");
    	if(username == null) {
    		return false;
    	}
    	return true;
    }
    
    public static void index() {
        render();
    }

	public static void hiw() {
    	render();
    }

    public static void how() {
    	render();
    }
    
    public static void setup() {
    	render();
    }
    
    public static void postKey(String key) throws Throwable {
    	String username = session.get("username");
    	if(username == null) {
    		session.put("key", key);
    		Secure.login();
    	}
    	
    	//username is in session so they are logged in, fetch the user...
    	EmailToUserDbo ref = NoSql.em().find(EmailToUserDbo.class, username);
    	if(ref == null) {
    		log.warn("should not be here, bug");
    		unauthorized("should not end up here, bug");
    	}
    	
    	UserDbo user = NoSql.em().find(UserDbo.class, ref.getValue());
    	
    	CellPhone c = NoSql.em().find(CellPhone.class, key);
    	if(c == null) {
    		log.info("key is invalid="+key);
    		validation.addError("key", "Your key is invalid");
    	}
    	
    	if(validation.hasErrors()) {
    		setup();
    	}
    	
    	user.addPhone(c);
    	
    	NoSql.em().put(c);
    	NoSql.em().put(user);
    	
    	NoSql.em().flush();
    	
    	MyStuff.cellPhones();
    }
}