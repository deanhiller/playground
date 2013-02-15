package com.playground.qatests;

/*
* Updated: Alex Estrada, PayJunction
* Date: April 14, 2010
* File: QuickLink.java
* Description: Implementation of the Trinity Gateway Service API v1.2 in Java.
* 

--Protecting your Website and Payment Applications from Man-In-The-Middle-Attacks--
Our API allows merchants and developers to connect to PayJunction via an encrypted SSL communications link.
 In order for your website or payment application to verify that your connection to PayJunction has not
  been intercepted, all code that connects to PayJunction must verify then authenticity of our SSL certificate
   by performing strict server certificate verification against PayJunction's root certificate authority.
    It has come to our attention that some merchants have disabled the SSL certificate verification for
     testing purpose; while the SSL connection remains secure, it is possible that an attacker could try
      to initiate a Man-In-The-Middle attack in an attempt to intercept your website's or
       applications connection to PayJunction.

Common Mistake: Anyone website or application that does not verify the authenticity
 of PayJunction's SSL certificate upon connecting to our QuickLink API.

Solution: Turn SSL verification on prior to connecting to PayJunction for transaction authorizations.
 In the event that your website is unable to verify the authenticity of PayJunction's
  root certificate, your website and/or application should not transmit any transaction
   information and should reject your customer's transactions.
    If you believe your application is unable to verify the authenticity of PayJunction's
     certificate, contact our support department.

--PayJunction Security Requirements and Best Practices for Merchants and Developers--
https://www.payjunction.com/trinity/support/view.action?knowledgeBase.knbKnowledgeBaseId=451

--Please note--
There is no warranty for the programs or example code provided in PayJunction's
 support center. The copyright holders and/or other parties provide the program
  "as is" without warranty of any kind, either expressed or implied, including,
   but not limited to, the implied warranties of merchantability and fitness
    for a particular purpose. The entire risk as to the quality and performance
     of the program is with you. Should the program prove defective, you assume
      the cost of all necessary servicing, repair or correction. It is the merchant's
       responsibility to ensure their systems meet the PCI requirements. PayJunction's
        Gateway Agreement have been updated to include the referenced "PayJunction
         Security Requirements and Best Practices for Merchants and Developers;" it
          is the merchant's responsibility to review this knowledge base on a
           regular basis for the security of the merchant's account and to protect cardholder data.

Please contact us if you have any questions.

With Kindest Regards,
The PayJunction Team
support@payjunction.com

********************************************/

import java.net.*;
import java.io.*;
import java.util.*;

import javax.net.ssl.HttpsURLConnection;

/*
 * Runs credit card transactions through the internet. Can authorize, charge, or refund
 * a credit card by forming and submitting a Trinity QuickLink API request.  Can perform
 * one-time transactions, recurring transactions, recharge cards from previous transactions
 * via a transaction ID (instant transaction), dynamically control AVS and CVV security 
 * settings, update a transaction posture before batch, and control batch settlement.  
 * Does this by formulating a standard HTTPS request and submitting it to the PayJunction
 * payment server. Computer requirements: must be connected to the internet and be able 
 * to communicate via TCP/IP with port 443 of the PayJunction server.
 * @author Erin Howard
 */
//Used to be called QuickLink.java
public class CreditCardProcessor 
{
	private String security = "";
	private String logon, password;

	/**
	 * Loads the username and password for this account
	 * @param logon - QuickLink Login identifying your account
	 * @param password - QuickLink Password for your account
	 */
	public CreditCardProcessor (String logon, String password)
	{
		this.logon = logon;
		this.password = password;
	}
	
	/**
	 * Dynamically controls the AVS and CVV independent of the settings in the web interface.
	 * Can be applied to one-time transactions, instant transactions, recurring transactions,
	 * and recurring instant transactions.  A value string is formed with each entry delimited
	 * by the "|" character in the format AVS|CVV|PREAUTH|AVSFORCE|CVVFORCE.
	 * @param newAVS Address Verification System (possible values: AWZ, XY, WZ, AW, AZ, A, X, Y, W, Z)
	 * @param newCVV Cardholder Verification Number
	 * @param newPreAuth Pre-authorize card
	 * @param newAVSforce Override AVS
	 * @param newCVVforce Override CVV
	 */
	public void setSecurity(String newAVS, String newCVV, String newPreAuth, String newAVSforce, String newCVVforce)
	{
		//if all values are given, update security variable so that later the
		//new security value can be sent to Trinity Gateway Service
		if(newAVS!="" && newCVV!="" && newPreAuth!="" && newAVSforce!="" && newCVVforce!="")
		{
			security = newAVS + "|" + newCVV + "|" + newPreAuth + "|" + newAVSforce + "|" + newCVVforce;
			System.out.println("security settings changed: " + security);
		}
		//if any of the arguments are empty, do not update security.  The reason
		//for this is that one might expect that any values left blank will remain
		//the same as the settings in the web interface.  In reality they cause 
		//Trinity to revert the relevant security setting to the default value which 
		//is not necessarily secure
		else
			System.out.println("no change in security - must provide all values");
	}
	
	/**
	 * Encodes a Trinity QuickLink argument as an application/x-www-form-urlencoded form data set.
	 * @param name - Variable name familiar to Trinity QuickLink (of the format dc_XXX)
	 * @param value - Value collected from card to perform required transaction
	 * @return - Formatted string with name and value
	 */
	private String encode (String name, String value)
	{
		try{
			//return URL encoded string such that it can later be concatenated
			//with the string being sent to Trinity Gateway Service
			if (value != null && value.length() >= 1)
			{
				return ("&" + URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8"));
			}
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return "";
	}
	
	public HashMap authorize(CreditCard card, String amount) {
		return authorize(card.getName(), card.getNumber(), card.getExp_month(), card.getExp_year(), 
				card.getCvs(), card.getAddress(), card.getCity(), card.getState(), card.getZip(), amount, "");
	}
	
	public HashMap authorize(String transactionId, String amount) {
		return authorize("", "", "", "", "", "", "", "", "", amount, transactionId);
	}
	/**
	 * Requests authorization of card from Trinity QuickLink Information
	 * @param name - The name of the cardholder
	 * @param number - The 13-16 digit credit card number
	 * @param exp_month - The two digit expiration month of the credit card
	 * @param exp_year - The two digit expiration year of the credit card
	 * @param cvs - The card verification value of the credit card, a three- or four-digit number
	 * @param address - The street address of the cardholder
	 * @param city - The city of the address of the cardholder
	 
	 * @param state - The state of the address of the cardholder
	 * @param zip - The zip or postal code of the address of the cardholder
	 * @param amount - The amount to transact
	 * @param transaction_id - The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @return - Results returned by Trinity QuickLink Service for the desired transaction
	 */
	private HashMap authorize 
	(
		String name, String number, String exp_month, String exp_year, 
		String cvs, String address, String city, String state, String zip, String amount, String transaction_id
	)
	{
		//process one-time or instant authorize request on credit card
		return process("AUTHORIZATION", address, city, state, zip, amount, name, number, exp_month, 
				exp_year, cvs, "", "", "", "", "", transaction_id, "");
	}
	
	public Result charge(CreditCard card, String amount) {
		HashMap result = chargeImpl(card, amount);
		return new Result(result);
	}
	public HashMap chargeImpl(CreditCard card, String amount) {
		return charge(card.getName(), card.getNumber(), card.getExp_month(), card.getExp_year(), 
				card.getCvs(), card.getAddress(), card.getCity(), card.getState(), card.getZip(), 
				amount, "");
	}

	public HashMap charge(String transactionId, String amount) {
		return charge("", "", "", "", "", "", "", "", "", amount, transactionId);
	}
	
	/**
	 * Requests charge from Trinity QuickLink Information
	 * @param name - The name of the cardholder
	 * @param number - The 13-16 digit credit card number
	 * @param exp_month - The two digit expiration month of the credit card
	 * @param exp_year - The two digit expiration year of the credit card
	 * @param cvs - The card verification value of the credit card, a three- or four-digit number
	 * @param address - The street or post office box for the address of the cardholder
	 * @param city - The city of the address of the cardholder
	 
	 * @param state The state of the address of the cardholder
	 * @param zip - The zip or postal code of the address of the cardholder
	 * @param amount - The amount to transact
	 * @param transaction_id - The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @return Results returned by Trinity QuickLink Service for the desired transaction
	 */
	private HashMap charge 
	(
		String name, String number, String exp_month, String exp_year, 
		String cvs, String address, String city, String state, String zip, String amount, String transaction_id
	)
	{
		//process one-time or instant charge request on credit card
		return process("AUTHORIZATION_CAPTURE", address, city, state, zip, amount, name, number, 
				exp_month, exp_year, cvs, "", "", "", "", "", transaction_id, "");
	}

	public HashMap refund(CreditCard card, String amount) {
		return refund(card.getName(), card.getNumber(), card.getExp_month(), card.getExp_year(), 
				card.getCvs(), card.getAddress(), card.getCity(), card.getState(), card.getZip(), amount, "");
	}
	
	public HashMap refund(String transactionId, String amount) {
		return refund("", "", "", "", "", "", "", "", "", amount, transactionId);
	}
	/**
	 * Requests refund from Trinity QuickLink Information
	 * @param name - The name of the cardholder
	 * @param number - The 13-16 digit credit card number
	 * @param exp_month - The two digit expiration month of the credit card
	 * @param exp_year - The two-digit expiration year of the credit card
	 * @param cvs - The card verification value of the credit card, a three- or four-digit number
	 * @param address - The street or post office box for the address of the cardholder
	 * @param city - The city of the address of the cardholder
	 
	 * @param state - The state of the address of the cardholder
	 * @param zip - The zip or postal code of the address of the cardholder
	 * @param amount - The amount to transact
	 * @param transaction_id The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @return Results returned by Trinity QuickLink Service for the desired transaction
	 */
	private HashMap refund
	(
		String name, String number, String exp_month, String exp_year, 
		String cvs, String address, String city, String state, String zip, String amount, String transaction_id
	)
	{
		//process one-time or instant refund request on credit card
		return process("CREDIT", address, city, state, zip, amount, name, number, exp_month, 
				exp_year, cvs, "", "", "", "", "", transaction_id, "");
	}
	
	/**
	 * Requests recurring authorization from Trinity QuickLink Information. The card for
	 * the transaction will be billed according to the schedule specified. The recurring
	 * transaction will also be accessible from the Trinity Point of Sale system at
	 * http://www.PayJunction.com.  Note: subsequent editing and management of the 
	 * recurring transaction must be made from the Trinity Point of Sale web system.
	 * @param name - The name of the cardholder
	 * @param number - The 13-16 digit credit card number
	 * @param exp_month - The two-digit expiration month of the credit card
	 * @param exp_year - The two-digit expiration year of the credit card
	 * @param cvs - The card verification value of the credit card, a three- or four-digit number
	 * @param address - The street or post office box for the address of the cardholder
	 * @param city - The city of the address of the cardholder
	 
	 * @param state - The state of the address of the cardholder
	 * @param zip - The zip or postal code of the address of the cardholder
	 * @param amount - The amount to transact
	 * @param transaction_id - The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @param create - Needs to be set to "true" in order to create a new recurring transaction
	 * @param limit - Number of approved transactions you want to run on the schedule
	 * @param periodic_number - Unit length of time between transactions
	 * @param periodic_type - "day", "week", "month"
	 * @param start - YYYY-MM-DD Note: must be a valid date that exists
	 * @return - Results returned by Trinity QuickLink Service for the desired transaction
	 */
	public HashMap scheduleAuthorize
	(
		String name, String number, String exp_month, String exp_year, 
		String cvs, String address, String city, String state, String zip, String amount, String transaction_id,
		String create, String limit, String periodic_number, String periodic_type, String start	
	)
	{
		//process one-time or instant authorize request on recurring transaction of credit card
		return process("AUTHORIZATION", address, city, state, zip, amount, name, number, exp_month, 
				exp_year, cvs, create, limit, periodic_number, periodic_type, start, transaction_id, "");	
	}
	
	public HashMap scheduleRecurringCharge(CreditCard card, RecurringInfo info) {
		return scheduleCharge(card.getName(), card.getNumber(), card.getExp_month(), card.getExp_year(), 
				card.getCvs(), card.getAddress(), card.getCity(), card.getState(), card.getZip(), 
				info.getAmount(), "", info.getCreate(),
				info.getLimit(), info.getPeriodic_number(), info.getPeriodic_type(), info.getStartDate());
	}
	
	public HashMap scheduleRecurringCharge(String transactionId, RecurringInfo info) {
        //HashMap response4 = scheduleCharge ("", "", "", "", "", "8320 Test St", "Santa Barbara", "Ca", "85284", "2.03", transaction_id2, "true", "5", "2", "week", "2010-12-02");
		//HashMap response4 = scheduleCharge ("", "", "", "", "", "", "", "", "", "2.03", transaction_id2, "true", "5", "2", "week", "2010-12-02");
		return scheduleCharge("", "", "", "", "", "", "", "", "", info.getAmount(), transactionId, info.getCreate(),
				info.getLimit(), info.getPeriodic_number(), info.getPeriodic_type(), info.getStartDate());
	}
	
	/**
	 * Requests recurring charge from Trinity QuickLink Information.  The card for the
	 * transaction will be billed according to the schedule specified.  The recurring
	 * transaction will also be accessible from the Trinity Point of Sale system at
	 * http://www.PayJunction.com.  Note: subsequent editing and management of the 
	 * recurring transaction must be made from the Trinity Point of Sale web system.
	 * @param name - The name of the cardholder
	 * @param number - The 13-16 digit credit card number
	 * @param exp_month - The two-digit expiration month of the credit card
	 * @param exp_year - The two-digit expiration year of the credit card
	 * @param cvs - The card verification value of the credit card, a three- or four-digit number
	 * @param address - The street or post office box for the address of the cardholder
	 * @param city - The city of the address of the cardholder
	 
	 * @param state The state of the address of the cardholder
	 * @param zip - The zip or postal code of the address of the cardholder
	 * @param amount - The amount to transact
	 * @param transaction_id - The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @param create - Needs to be set to "true" in order to create a new recurring transaction
	 * @param limit - Number of approved transactions you want to run on the schedule
	 * @param periodic_number - Unit length of time between transactions
	 * @param periodic_type - "day", "week", "month"
	 * @param start - YYYY-MM-DD Note: must be a valid date that exists
	 * @return - Results returned by Trinity Gateway Service for the desired transaction
	 */
	private HashMap scheduleCharge
	(
		String name, String number, String exp_month, String exp_year, 
		String cvs, String address, String city, String state, String zip, String amount, String transaction_id,
		String create, String limit, String periodic_number, String periodic_type, String start	
	)
	{
		//process one-time or instant charge request on recurring transaction of credit card
		return process("AUTHORIZATION_CAPTURE", address, city, state, zip, amount, name, number, exp_month, 
				exp_year, cvs, create, limit, periodic_number, periodic_type, start, transaction_id, "");
	}
	
	/**
	 * Requests recurring refund from Trinity Gateway Information.  The card for the
	 * transaction will be billed according to the schedule specified.  The recurring
	 * transaction will also be accessible from the Trinity Point of Sale system at
	 * http://www.PayJunction.com.  Note: subsequent editing and management of the 
	 * recurring transaction must be made from the Trinity Point of Sale web system.
	 * @param name - The name of the cardholder
	 * @param number - The 13-16 digit credit card number
	 * @param exp_month - The two-digit expiration month of the credit card
	 * @param exp_year - The two-digit expiration year of the credit card
	 * @param cvs - The card verification value of the credit card, a three- or four-digit number
	 * @param address - The street or post office box for the address of the cardholder
	 * @param city - The city of the address of the cardholder
	 
	 * @param state The state of the address of the cardholder
	 * @param zip - The zip or postal code of the address of the cardholder
	 * @param amount - The amount to transact
	 * @param transaction_id - The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @param create - Needs to be set to "true" in order to create a new recurring transaction
	 * @param limit - Number of approved transactions you want to run on the schedule
	 * @param periodic_number - Unit length of time between transactions
	 * @param periodic_type - "day", "week", "month"
	 * @param start - YYYY-MM-DD Note: must be a valid date that exists
	 * @return - Results returned by Trinity QuickLink Service for the desired transaction
	 */
	public HashMap scheduleRefund
	(
		String name, String number, String exp_month, String exp_year, 
		String cvs, String address, String city, String state, String zip, String amount, String transaction_id, String create,
		String limit, String periodic_number, String periodic_type, String start	
	)
	{
		//process one-time or instant refund request on recurring transaction of credit card
		return process("CREDIT", address, city, state, zip, amount, name, number, exp_month, 
				exp_year, cvs, create, limit, periodic_number, periodic_type, start, transaction_id, "");
	}

	/**
	 * Updates the posture of a transaction before the batch closes.  Commonly used to
	 * void a transaction or to toggle a transaction from authorization (hold) to capture.
	 * @param posture - "capture", "void", "hold"
	 * @param transaction_id - The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @return - Results returned by Trinity Gateway Service for the desired transaction
	 */
	public HashMap posture
	(
		String posture, String transaction_id
	)
	{
		//process request to update the posture of a transaction
		return process ("update", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", transaction_id, posture);
	}
	
	/**
	 * Initiates settlement independent of the web settings.  Note: this feature should
	 * only be used by advanced clients that need custom settlement behaviors.
	 * @return - Results returned by Trinity Gateway Service for the desired transaction
	 */
	public HashMap settlement()
	{
		//process request to initiate settlement
		return process("settle", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
	}
	
	/**
	 * Processes and sends a URL encoded form data set of the information
	 * taken from the credit card for transactions and other requests, 
	 * such as posture update and settlement, in URL encoded format.
	 * @param transaction_type - The action to take on the transaction (Possible actions:
	 * 		AUTHORIZATION, AUTHORIZATION_CAPTURE, CREDIT)
	 * @param address - The street or post office box for the address of the cardholder
	 * @param city - The city of the address of the cardholder
	 
	 * @param state - The state of the address of the cardholder
	 * @param zip - The zip or postal code of the address of the cardholder
	 * @param amount - The amount to transact
	 * @param name - The name of the cardholder
	 * @param number - The 13-16 digit credit card number
	 * @param exp_month - The two-digit expiration month of the credit card
	 * @param exp_year - The two-digit expiration year of the credit card
	 * @param cvs - The card verification value of the credit card, a three- or four-digit number
	 * @param create - Needs to be set to "true" in order to create a new recurring transaction
	 * @param limit - Number of approved transactions you want to run on the schedule
	 * @param periodic_number - Unit length of time between transactions
	 * @param periodic_type - "day", "week", "month"
	 * @param start - YYYY-MM-DD Note: must be a valid date that exists
	 * @param transaction_id - The transaction ID returned from a previous transaction (uses
	 * 		name, number, exp_month, exp_year, and cvs of previous transaction)
	 * @param posture - "capture", "void", "hold"
	 * @return
	 */
	private HashMap process 
	(
		String transaction_type, String address, String city, String state, String zip, String amount,
		String name, String number, String expiration_month, String expiration_year,
		String cvs, String create, String limit, String periodic_number, String periodic_type,
		String start, String transaction_id, String posture
	)
	{
		HashMap<String, String> response_hash = new HashMap<String, String> ();		// Use with Java 1.5 and later
		//HashMap response_hash = new HashMap();		// Use with Java 1.4 and earlier
		try 
		{
			//build encoded string to send to Trinity QuickLink API Service
			//any empty arguments ("") are not added to the string,
			//because this sometimes causes conflicts with Trinity
			String data = encode("dc_logon", logon);
			data += encode("dc_password", password);
			data += encode("dc_transaction_type", transaction_type);
			data += encode("dc_version", "1.2");
			data += encode("dc_address", address);
			data += encode("dc_city", city);
			
			data += encode("dc_state", state);
			data += encode("dc_zipcode", zip);
			data += encode("dc_transaction_amount", amount);
			data += encode("dc_name", name);
			data += encode("dc_number", number);
			data += encode("dc_expiration_month", expiration_month);
			data += encode("dc_expiration_year", expiration_year);
			data += encode("dc_verification_number", cvs);
			data += encode("dc_schedule_create", create);
			data += encode("dc_schedule_limit", limit);
			data += encode("dc_schedule_periodic_number", periodic_number);
			data += encode("dc_schedule_periodic_type", periodic_type);
			data += encode("dc_schedule_start", start);
			data += encode("dc_transaction_id", transaction_id);
			data += encode("dc_posture", posture);

			//if security was updated, add it to the data string
			if(security != ""){
				System.out.println("updated security");
				data += "&" + URLEncoder.encode("dc_security", "UTF-8") + "=" + URLEncoder.encode(security, "UTF-8");
			}
			
			// URL for PayJunction to perform Test transactions
			URL url = new URL("https://www.payjunctionlabs.com/quick_link");

			// URL for PayJunction to perform Live Transactions
			// URL url = new URL("https://www.payjunction.com/quick_link");
			
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setDoOutput (true);
			conn.setDoInput (true);
			conn.setRequestMethod ("POST");
			conn.setAllowUserInteraction (false);

			//send data string to PayJunction so it can be processed and implemented
			OutputStreamWriter out  = new OutputStreamWriter (conn.getOutputStream());
			out.write (data);
			out.flush ();
			out.close ();

			BufferedReader in = new BufferedReader(new InputStreamReader (conn.getInputStream()));
			StringBuffer response = new StringBuffer();
			String line = in.readLine ();
			
			//read in results of requests
			while (line != null)
			{
				response.append (line);
				line = in.readLine ();
			}
			in.close ();

			StringTokenizer st1 = 
				new StringTokenizer (response.toString(), new Character ((char)0x1C).toString ());

			//process results so they are in an easy-to-access format
			while (st1.hasMoreTokens ())
			{       
				String key = null, value  = null;

				StringTokenizer st2 = new StringTokenizer (st1.nextToken (), "=");
				key = st2.nextToken ();

				if (st2.hasMoreElements ())
				{       
					value = st2.nextToken ();
				}
				response_hash.put (key, value);
			}
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
		
		//return results of request
		return response_hash;
	}
	
	/**
	 * Prints results stored in the response returned by Trinity QuickLink Service 
	 * @param response - Value returned by Trinity QuickLink Service
	 */
	public static void printResults(HashMap response)
	{
		Set keys = response.keySet();
		Iterator i = keys.iterator();
		
		Object key;
		
		while(i.hasNext()){
			key = i.next();
			//if(response.get(key) != null && !((String)response.get(key)).matches("null"))
				System.out.println (key + " = " + response.get(key));
		}
	}
	
	/*********************/
	
	public static void main (String args [])
	{
		//runRecurring();
		//runCharges();
		runCharges2();
	}

	private static void runCharges2() {
		QuickLink ql = new QuickLink ("pj-ql-01", "pj-ql-01p");

		//to change security settings:
		//ql.setSecurity("A", "M", "false", "true", "false");
		//commented out so that following transactions can be performed
		//without security changes, as is the most common case
		
		System.out.println("Normal Transaction:");
		
		HashMap response1 = ql.Charge ("Dean Bobx", "4444333322221111", "12", "12", "999", "8320 Test St", "Broomfield", "Co", "80200", "2.20", "");
		
		//save transaction ID to use with instant transaction
		String transaction_id1 = (String) response1.get ("dc_transaction_id");
		
		//print the results of the request
        printResults(response1);
                
        System.out.println("\nInstant Transaction:");
        
       	HashMap response2 = ql.Charge ("", "", "", "", "", "", "", "", "", "2.21", transaction_id1);
       	//&dc_logon=pj-ql-01&dc_password=pj-ql-01p
       	//&dc_transaction_type=AUTHORIZATION_CAPTURE&dc_version=1.2&dc_transaction_amount=2.11&dc_transaction_id=394961
       	
       	//print the results of the request
       	printResults(response2);

        System.out.println("\nRecurring Transaction:");
        
        HashMap response3 = ql.Schedule_Charge ("Dean Bobw", "4444333322221111", "12", "12", "999", "8320 Test St", "Broomfield", "Co", "80200", "2.23", "", "true", "5", "1", "month", "2012-02-15");
        
		//save transaction ID to use with instant transaction
        String transaction_id2 = (String) response3.get (new String ("dc_transaction_id"));
        
       	//print the results of the request
        printResults(response3);

        System.out.println("\nRecurring Instant Transaction:");
        
        HashMap response4 = ql.Schedule_Charge ("", "", "", "", "", "8320 Test St", "Broomfield", "Co", "80020", "2.24", transaction_id2, "true", "5", "2", "week", "2012-02-14");
	    
       	//print the results of the request
        printResults(response4);
        
        System.out.println("\nTransaction Posture Update (Void) - Transaction ID: " + transaction_id1);
        
        HashMap response5 = ql.Posture("void", transaction_id1);

       	//print the results of the request
        printResults(response5);
        
        System.out.println("\nSettlement");
        
        //might take a little time to initiate settlement
        HashMap response6 = ql.Settlement();
        
       	//print the results of the request
        printResults(response6);
        
		System.out.println( "finished" );		
	}

	private static void runRecurring() {
		CreditCardProcessor ql = new CreditCardProcessor ("pj-ql-01", "pj-ql-01p");

		CreditCard card = new CreditCard();
		card.setName("Dean Catalina");
		card.setNumber("4444333322221111");
		card.setExp_month("12");
		card.setExp_year("12");
		card.setCvs("999");
		card.setAddress("8320 Test St");
		card.setCity("Santa Barbara");
		card.setState("Ca");
		card.setZip("85284");
                
        System.out.println("\nRecurring Transaction:");

		RecurringInfo info = new RecurringInfo();
		info.setAmount("7.18");
		info.setCreate("true");
		info.setLimit("12");
		info.setPeriodic_number("1");
		info.setPeriodic_type("month");
		info.setStartDate("2012-02-15");

		HashMap response1 = ql.scheduleRecurringCharge(card, info);
		//save transaction ID to use with instant transaction
		String transaction_id1 = (String) response1.get ("dc_transaction_id");
		
		//print the results of the request
        printResults(response1);
        
        System.out.println("\nRecurring Transaction byTxId:");

		RecurringInfo info2 = new RecurringInfo();
		info2.setAmount("7.19");
		info2.setCreate("true");
		info2.setLimit("12");
		info2.setPeriodic_number("1");
		info2.setPeriodic_type("month");
		info2.setStartDate("2012-02-15");

		HashMap response4 = ql.scheduleRecurringCharge(transaction_id1, info2);
	    
       	//print the results of the request
        printResults(response4);		
	}
	
	private static void runCharges() {
		CreditCardProcessor ql = new CreditCardProcessor ("pj-ql-01", "pj-ql-01p");

		//to change security settings:
		//ql.setSecurity("A", "M", "false", "true", "false");
		//commented out so that following transactions can be performed
		//without security changes, as is the most common case

		System.out.println("Normal Transaction:");

		CreditCard card = new CreditCard();
		card.setName("Dean Catalina");
		card.setNumber("4444333322221111");
		card.setExp_month("12");
		card.setExp_year("12");
		card.setCvs("999");
		card.setAddress("8320 Test St");
		card.setCity("Santa Barbara");
		card.setState("Ca");
		card.setZip("85284");
		HashMap response1 = ql.chargeImpl(card, "6.22");
		
		//save transaction ID to use with instant transaction
		String transaction_id1 = (String) response1.get ("dc_transaction_id");
		
		//print the results of the request
        printResults(response1);
                
        System.out.println("\nInstant Transaction:");
        
       	HashMap response2 = ql.charge (transaction_id1, "6.23");
       	
       	//print the results of the request
       	printResults(response2);

        System.out.println("\nRecurring Transaction:");

		RecurringInfo info = new RecurringInfo();
		info.setAmount("7.04");
		info.setCreate("true");
		info.setLimit("12");
		info.setPeriodic_number("1");
		info.setPeriodic_type("month");
		info.setStartDate("2012-02-14");
        
        //HashMap response4 = ql.scheduleCharge ("", "", "", "", "", "8320 Test St", "Santa Barbara", "Ca", "85284", "2.03", transaction_id2, "true", "5", "2", "week", "2010-12-02");
	    HashMap response4 = ql.scheduleRecurringCharge(transaction_id1, info);
	    
       	//print the results of the request
        printResults(response4);
        
//        System.out.println("\nTransaction Posture Update (Void) - Transaction ID: " + transaction_id1);
//        
//        HashMap response5 = ql.posture("void", transaction_id1);
//
//       	//print the results of the request
//        printResults(response5);
//        
//        System.out.println("\nSettlement");
//        
//        //might take a little time to initiate settlement
//        HashMap response6 = ql.settlement();
//        
//       	//print the results of the request
//        printResults(response6);
//        
//		System.out.println( "finished" );
	}
}

