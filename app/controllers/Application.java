package controllers;

import java.util.Random;

import models.CellPhone;
import models.EmailToUserDbo;
import models.UserDbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.play.NoSql;

import controllers.auth.Secure;

import play.Play;
import play.mvc.Controller;

public class Application extends Controller {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static Random r = new Random(System.currentTimeMillis());
	private static long counter1;
	private static long counter2;
	
    public static boolean isLoggedIn() {
    	String username = session.get("username");
    	if(username == null) {
    		return false;
    	}
    	return true;
    }
    public static boolean isProdMode() {
    	String mode = Play.configuration.getProperty("application.mode");
    	if("prod".equals(mode))
    		return true;
    	return false;
    }
    
    public static void index() {
    	String val = session.get("index");
    	if("1".equals(val))
    		index1();
    	else if("2".equals(val))
    		index2();

    	int number = r.nextInt(2);
    	if(number == 0) {
        	incrementCounter1();
    		index1();
    	} else {
    		incrementCounter2();
    		index2();
    	}
    }

    public static void index1() {
    	session.put("index", "1");
        render();
    }
	public static void index2() {
		session.put("index", "2");
        render();
    }
    
    private synchronized static void incrementCounter1() {
		counter1++;
		log.info("count page1="+counter1);
	}
    private synchronized static void incrementCounter2() {
		counter2++;
		log.info("count page1="+counter2);
	}
    
	public static void hiw() {
    	render();
    }
	
	public static void about() {
	render();
    }

    public static void how() {
    	render();
    }
    
    public static void setup() {
    	render();
    }

	public static void forgot() {
    	render();
    }
	public static void register() {
    	render();
    }

	public static void postKey(String key) throws Throwable {
		validation.required(key);
		String username = session.get("username");
		if (username == null) {
			session.put("key", key);
			CellPhone key2 = NoSql.em().find(CellPhone.class, key);
			if (key2 == null) {
				validation.addError("key", "Your key is invalid");
			}
			if (validation.hasErrors()) {
				validation.keep();
				setup();
			} else
				Secure.login();
		}
    	
    	//username is in session so they are logged in, fetch the user...
    	EmailToUserDbo ref = NoSql.em().find(EmailToUserDbo.class, username);
    	if(ref == null) {
    		session.remove("username");
    		session.put("key", key);
    		log.warn("should not be here, bug");
    		Secure.login();
    	}
    	
    	UserDbo user = NoSql.em().find(UserDbo.class, ref.getValue());
    	
    	CellPhone c = NoSql.em().find(CellPhone.class, key);
    	if(c == null) {
    		log.info("key is invalid="+key);
    		validation.addError("key", "Your key is invalid");
    	}
    	
    	if(validation.hasErrors()) {
    		params.flash(); // add http parameters to the flash scope
    		validation.keep(); // keep the errors for the next request
    		setup();
    	}
    	
    	user.addPhone(c);
    	
    	NoSql.em().put(c);
    	NoSql.em().put(user);
    	
    	NoSql.em().flush();
    	
    	MyStuff.cellPhones();
    }
}