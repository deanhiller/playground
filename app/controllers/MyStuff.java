package controllers;

import java.util.List;

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

	public static void cellPhones() {
		UserDbo user = Utility.fetchUser();
		List<CellPhone> phones = user.getPhones();
		
		render(user, phones);
	}

	public static void accountsettings() {
        render();
    }
	
	
	public static void cell(String number) {
		NumberToCell ref = NoSql.em().find(NumberToCell.class, number);
		if(ref == null)
			notFound();
		
		CellPhone phone = NoSql.em().find(CellPhone.class, ref.getValue());
		
		List<TimePeriodDbo> periods = phone.getPeriods();
		for(TimePeriodDbo p : periods) {
			p.getBeginOfMonth();
		}
		render(phone);
	}

	public static void texts(String number, long time) {
		String id = TimePeriodDbo.formKey(number, time);
		TimePeriodDbo period = NoSql.em().find(TimePeriodDbo.class, id);
		
		render(period);
	}
	
    public static void completeRegister(String number) {
    	render(number);
    }
    
    public static void makePayment(String number) {
    	render(number);
    }
    
    public static void postPayment(String number) {

    	MyStuff.cell(number);
    }
}
