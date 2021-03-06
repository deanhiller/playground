package com.playground.qatests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.DefaultHttpClient;

public class SendTextMessageIn {

	public static void main(String[] args) throws UnsupportedEncodingException, ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		try {
			runLoop(httpclient);
		} catch(DoneException e) {
			return;
		}
	}

	private static void runLoop(DefaultHttpClient httpclient)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		while(true) {
			Scanner s = new Scanner(System.in);
			System.out.println("Please enter the phone to send a text to(enter exit to exit)");
			String sendTo = readValue(s);
			System.out.println("Please enter the key of the phone to send from(enter exit to exit)");
			String key = readValue(s);
			System.out.println("Enter r if message is received, s if message is sent");
			String direction = readValue(s);
			System.out.println("Please enter the text message");
			String msg = readValue(s);
			long time = System.currentTimeMillis();

			boolean isOutgoing;
			if(direction.equalsIgnoreCase("r"))
				isOutgoing = false;
			else
				isOutgoing = true;
				
			Utility.postMessage(httpclient, null, sendTo, msg, key, time, isOutgoing);
		}
	}

	private static String readValue(Scanner s) {
		String line = s.nextLine();
		if("exit".equals(line))
			throw new DoneException();
		return line;
	}
}
