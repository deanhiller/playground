package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import models.UserDbo;
import play.Play;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.play.NoSql;

import controllers.auth.Secure;

public class Security extends Secure.Security {

	private static final Logger log = LoggerFactory.getLogger(Security.class);
	
	static boolean authenticate(String username, String password) {
		if(username == null)
			unauthorized("username cannot be null");
		else if(password == null)
			unauthorized("password cannot be null");
		
		//We have some of our own users not in ldap for development only so check entity manager first
		String prop = Play.configuration.getProperty("application.mode");
		if("dev".equals(prop)) {
			UserDbo u = findExistingUser(username);
			if(u != null) {
				addToSession(username, u);
				return true;
			}
		}

		UserDbo user = NoSql.em().find(UserDbo.class, username);
		if(user == null) {
			log.info("username invalid="+username);
			unauthorized("username or password is invalid");
		} else if(!user.getPassword().equals(password)) {
			log.info("password invalid for user="+username);
			unauthorized("username or password is invalid");
		}
		
		// Add them to the session
		addToSession(username, user);
		return true;
	}

	private static void addToSession(String username, UserDbo user) {
		session.put("username", username);
	}

	private static UserDbo findExistingUser(String username) {
		NoSqlEntityManager mgr = NoSql.em();
		UserDbo user = mgr.find(UserDbo.class, username);
		return user;
	}

}
