package controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.play.NoSql;

import models.CellPhone;
import models.NumberToCell;
import models.TextMessageDbo;
import models.TimePeriodDbo;
import models.UserDbo;
import controllers.auth.Check;
import controllers.auth.Secure;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class MyStuff extends Controller {

	private static final Logger log = LoggerFactory.getLogger(MyStuff.class);
	
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

	public static void settings() {
        render();
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
    	render(number);
    }

    public static void postPayment(String number) {
    	CellPhone phone = lookupCell(number);
    	phone.setPaid(true);
    	NoSql.em().put(phone);
    	NoSql.em().flush();
    	
    	MyStuff.cell(number);
    }
}
