package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.play.NoSql;
import com.playground.qatests.CreditCard;
import com.playground.qatests.CreditCardProcessor;
import com.playground.qatests.CreditCardProcessorImpl;
import com.playground.qatests.RecurringInfo;
import com.playground.qatests.Result;

import models.CellPhone;
import models.EmailToUserDbo;
import models.NumberToCell;
import models.TextMessageDbo;
import models.TimePeriodDbo;
import models.UserDbo;
import controllers.auth.Check;
import controllers.auth.Secure;
import controllers.auth.Secure.Security;
import play.Play;
import play.data.validation.Required;
import play.libs.Crypto;
import play.libs.Time;
import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Scope.Session;

@With(Secure.class)
public class MyStuff extends Controller {

	private static final Logger log = LoggerFactory.getLogger(MyStuff.class);

	private static int monthlyPriceCents = 700;
	
	private static CellPhone lookupCell(String number) {
		UserDbo user = Utility.fetchUser();
		List<CellPhone> phones = user.getPhones();
		for(CellPhone phone : phones) {
			if(number.equals(phone.getPhoneNumber())) {
				log.info("lookup cell="+number+" found for user="+user.getEmail());
				return phone;
			}
		}
		
		log.info("lookup cell="+number+" not found");
		notFound("This phone doesn't exist or you don't have access to it");
		return null;
	}
		
	public static void cellPhones() {
		UserDbo user = Utility.fetchUser();
		List<CellPhone> phones = user.getPhones();
		
		render(user, phones);
	}

	public static void settings(String msg) {
		render(msg);
    }
	public static void Accountsettings(String password,String newpassword, String verifyPassword) throws Throwable {
		validation.required(password);
		validation.required(newpassword);
		validation.required(verifyPassword);
		String userName = Session.current().get("username");
		EmailToUserDbo emailToUser = NoSql.em().find(EmailToUserDbo.class, userName);
		String id1=emailToUser.getValue();
		UserDbo user = NoSql.em().find(UserDbo.class, id1);
	    String pass=user.getPassword();
	    if (password == null || newpassword ==null || verifyPassword == null) {
			validation.addError("oldpassword", "Please provide the required password.");
		}
		if (!pass.equals(password)) {
			validation.addError("password", "Your existing password did not match.");
		}
		if (!newpassword.equals(verifyPassword)) {
			validation.addError("verifyPassword", "New Password did not match");
		}

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			settings(null);
		} else {
			user.setPassword(newpassword);
			NoSql.em().put(user);
			NoSql.em().flush();
			settings("Your password has been updated successfully");
		}
	}

	public static void cell(String number) {
		CellPhone phone = lookupCell(number);
		
//		List<TimePeriodDbo> periods = phone.getPeriods();
//		for(TimePeriodDbo p : periods) {
//			p.getBeginOfMonth();
//		}
		render(phone);
	}

	public static void texts(String number, long time) {
		//for security lookup the cell phone
		lookupCell(number);
		String id = TimePeriodDbo.formKey(number, time);
		TimePeriodDbo period = NoSql.em().find(TimePeriodDbo.class, id);
		
//		for(TextMessageDbo txt : period.getMessages()) {
//			log.info("txt="+txt.isCanDisplay());
//		}
		
		render(period);
	}
	
    public static void completeRegister(String number) {
    	lookupCell(number);
    	render(number);
    }
    
    public static void makePayment(String number) {
    	lookupCell(number);
    	
    	int amount = calculateProrateAmount();
    	String amountStr = centsToDollars(amount);
    	
    	render(number, amountStr);
    }

    public static void postPayment(String number, CreditCard card, String phoneNumber, String amountStr) {
    	required("card.number", card.getNumber());
    	required("card.name", card.getName());
    	required("card.address", card.getAddress());
    	required("card.city", card.getCity());
    	required("card.cvv", card.getCvv());
    	required("card.expMonth", card.getExpMonth());
    	required("card.expYear", card.getExpYear());
    	required("card.state", card.getState());
    	required("card.zip", card.getZip());
    	required("phoneNumber", phoneNumber);

    	CellPhone phone = lookupCell(number);
    	
    	String mode = Play.configuration.getProperty("application.mode", "something");
    	CreditCardProcessor processor = new CreditCardProcessorImpl("pj-ql-01", "pj-ql-01p");
    	if("dev".equals(mode))
    		processor = new FakeCardProcessor();
    		
    	Result res = processor.charge(card, amountStr);
    	if(res.isFailed()) {
    		log.info("failure processing credit card.  phone="+phoneNumber+" failure="+res.getErrorMessage()+" all="+res.getMap());
        	validation.addError("none", "Failure processing credit card.  Payment Gateway response="+res.getErrorMessage());
    	}
//lkjo45 dean@xsoftware.biz
    	if(validation.hasErrors()) {
    		params.flash(); // add http parameters to the flash scope
    		validation.keep(); // keep the errors for the next request
    		renderTemplate("@makePayment", number, card);    		
    	}

    	phone.setCreditCardTxId(res.getTransactionId());
    	phone.setPaid(true);
    	NoSql.em().put(phone);
    	NoSql.em().flush();
    	
    	RecurringInfo info = new RecurringInfo();
    	info.setAmount(centsToDollars(monthlyPriceCents));
		//Now we need to try the recurring charge and make sure that works as well, but since the first charge worked
    	//we will set paid on the phone to true
    	Result resp = processor.scheduleRecurringCharge(res.getTransactionId(), info );
    	log.info("PAYMENT RESULTS="+resp.getMap());

    	MyStuff.cell(number);
    }

	private static void required(String field, String value) {
		if(value == null || "".equals(value))
			validation.addError(field, "This field is required");
	}

	private static String centsToDollars(int moneyInCents) {
		String s = ""+moneyInCents;
 		while(s.length() < 3) {
 			s = "0"+s;
 		}
 		String dollars = s.substring(0, s.length()-2);
 		String cents = s.substring(s.length()-2);
		return dollars+"."+cents;
	}

	private static int calculateProrateAmount() {
		DateTime time = DateTime.now();
		DateTime endOfMonth = time.dayOfMonth().withMaximumValue();
		DateTime beginOfMonth = time.dayOfMonth().withMinimumValue();
		Duration duration = new Duration(time, endOfMonth);
		Duration monthDuration = new Duration(beginOfMonth, endOfMonth);
		
		int numDaysTillNextMonth = (int) duration.getStandardDays();
		int numDaysMonth = (int) monthDuration.getStandardDays()+1;
		
		int prorated = monthlyPriceCents*numDaysTillNextMonth / numDaysMonth;
		return prorated;
	}
}
