package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import models.CellPhone;
import models.NumberToCell;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.play.NoSql;

import play.mvc.Controller;
import play.mvc.With;
import play.mvc.Scope.Session;
import controllers.auth.Secure;


public class Notify extends Controller {

	private static final Logger log = LoggerFactory.getLogger(Notify.class);

	public static void validate() throws Exception {
		log.info("IN NOTIFY URL11");
		String str = "cmd=_notify-validate&" + params.get("body");
		log.info(str);
		URL url = new URL("https://www.sandbox.paypal.com/us/cgi-bin/webscr");
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

		PrintWriter out = new PrintWriter(connection.getOutputStream());
		out.println(str);
		out.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String result = in.readLine();
		in.close();

		// 	assign posted variables to local variables
		String itemName = params.get("item_name");
		String itemNumber = params.get("item_number");
		String paymentStatus = params.get("payment_status");
		String paymentAmount = params.get("mc_gross");
		String paymentCurrency = params.get("mc_currency");
		String txnId = params.get("txn_id");
		String receiverEmail = params.get("receiver_email");
		String payerEmail = params.get("payer_email");
		
		NumberToCell numberCell = NoSql.em().find(NumberToCell.class, itemNumber);
		CellPhone cellPhone = NoSql.em().find(CellPhone.class, numberCell.getValue());
		log.info("RESULT is " + result);
		if("VERIFIED".equals(result)) {
			if ("Completed".equals(paymentStatus)) {
				if(cellPhone != null) {
					log.info("CHANGING STATUS TO PAID for number " + itemNumber);
					cellPhone.setPaid(true);
					cellPhone.setCreditCardTxId(txnId);
					NoSql.em().put(cellPhone);
					NoSql.em().flush();
				} else {
					log.info("Payment at paypal is completed but cellPhone= " + cellPhone + " is not found in sniffyapp");
				}
			} else {
				if(cellPhone != null) {
					cellPhone.setPaid(false);
					cellPhone.setCreditCardTxId(txnId);
					NoSql.em().put(cellPhone);
					NoSql.em().flush();
				}
			}		
		}
		else if("INVALID".equals(result)) {
			if(cellPhone != null) {
				cellPhone.setPaid(false);
				NoSql.em().flush();
			}
			else {
				log.info("Payment at paypal is NOT completed for cellPhone= " + cellPhone);
			}
		}
		else {
			log.info("Error with Paypal status");
		}
	}
}
