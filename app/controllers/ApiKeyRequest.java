package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import models.CellPhone;
import models.NumberToCell;
import models.TextMessageDbo;
import models.TimePeriodDbo;
import models.message.HeartBeat;
import models.message.NewKeyResponse;
import models.message.PutTextMessage;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Controller;
import play.mvc.Http.Request;
import play.mvc.results.BadRequest;

import com.alvazan.orm.api.base.spi.UniqueKeyGenerator;
import com.alvazan.play.NoSql;

public class ApiKeyRequest extends Controller {

	private static final Logger log = LoggerFactory.getLogger(ApiKeyRequest.class);
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final Random r = new Random(System.currentTimeMillis());

	private static synchronized String nextRandom() {
		long val = r.nextLong();
		String str = Long.toString(val, 36).toUpperCase();
		return str;
	}

	public static void newKey(String secureKey, String phoneNumber) {
		if(!("AAAAB3NzaC1kc3MAAACBAI0XYYyhYT861agRCv2BCIg6HjgARc3GnbmuXGkbrXzACzZAy1uQ6wte"+
	         "RDZpByiPVJaL8DKncf91QoFIBZKJ0ao7ZuOiCQ03VUfxb6YwMXMeLikjcSI+zRBh6NPP833mtYV"+
			  "pLG1kDpGGxmJdmt38iWvxqa9HJcLOzYQA6lqyPAAAAFQDUa2rnAC9arD905h42VwI2da+tawA"+
	          "AAIEAhzarb59ddJWTW831YZorBrpKPZp+WWAmO+4rjp82owQsI9aua4qfcSenb4+U").equals(secureKey)) {
			log.info("bad request for new key");
			badRequest("you hacker, get out of here.  The key is 320 BYTES not bits long so good luck ;)");
		}
		
		String key = UniqueKeyGenerator.generateKey().toUpperCase();
		String random = nextRandom();
		String all = key+random;
		all = all.replace(".", "");
		//let's insert a - every 4 characters to make it easier
		String newKey = "";
		int counter = 0;
		for(char c : all.toCharArray()) {
			newKey += c;
			counter++;
			if(counter % 4 == 0)
				newKey+="-";
		}

		if(newKey.endsWith("-"))
			newKey = newKey.substring(0, newKey.length()-1);
		
		CellPhone p = new CellPhone();
		p.setKey(newKey);
		p.setPhoneNumber(phoneNumber);
		NoSql.em().put(p);
		
		NumberToCell n = new NumberToCell();
		n.setId(phoneNumber);
		n.setValue(p.getKey());
		NoSql.em().put(n);
		
		NoSql.em().flush();

		NewKeyResponse resp = new NewKeyResponse();
		resp.setKey(newKey);
		String json = writeOut(resp);
		renderJSON(json);
	}

	private static String writeOut(NewKeyResponse resp) {
		try {
			return mapper.writeValueAsString(resp);
		} catch (JsonGenerationException e) {
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void postTextMessage() {
		String json = fetchJson();
		PutTextMessage msg = parseJson(json, PutTextMessage.class);

		String key = msg.getKey();

		DateTime timeReceived = new DateTime();
		TextMessageDbo msgDbo = create(msg, timeReceived);
		
		CellPhone cell = NoSql.em().find(CellPhone.class, key);
		if(cell == null) {
			log.info("cell phone not found, key="+key);
			badRequest("Your key is invalid.  It should be a very very large cert key");
		}

		DateTime beginOfMonth = timeReceived.withDayOfMonth(1).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0);
		TimePeriodDbo period = cell.getPeriod(beginOfMonth);
		if(period == null) {
			period = new TimePeriodDbo();
			period.setId(cell.getPhoneNumber(), beginOfMonth);
			NoSql.em().fillInWithKey(period);
		}

		//increase cell phone message count...
		cell.setMessageCount(cell.getMessageCount()+1);
		if(cell.getMessageCount() <= 3)
			msgDbo.setDisplayedForFree(true);
		
		period.addMessage(msgDbo);
		cell.addPeriod(period);

		NoSql.em().put(period);
		NoSql.em().put(msgDbo);
		NoSql.em().put(cell);
		NoSql.em().flush();
		
		ok();
	}

	private static TextMessageDbo create(PutTextMessage msg, DateTime timeReceived) {
		TextMessageDbo msgDbo = new TextMessageDbo();
		msgDbo.setTimeReceived(timeReceived);
		msgDbo.setCellTimeReceived(msg.getPhoneTime());
		msgDbo.setCellNumber(msg.getCellNumber());
		msgDbo.setRemoteNumber(msg.getRemoteNumber());
		msgDbo.setTextMessage(msg.getTextMessage());
		msgDbo.setOutgoing(msg.isOutgoing());
		NoSql.em().fillInWithKey(msgDbo);
		return msgDbo;
	}

	public static void heartBeat() {
		String json = fetchJson();
		HeartBeat hert = parseJson(json, HeartBeat.class);
		String key = hert.getKey();
		CellPhone phone = NoSql.em().find(CellPhone.class, key);
		if (phone != null) {
			phone.setLasttimestamp(System.currentTimeMillis());
			NoSql.em().put(phone);
			NoSql.em().flush();
		}
	}

	private static String fetchJson() {
		try {
			Request request = Request.current();
			InputStream in = request.body;
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer);
			String json = writer.toString();
			return json;
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static <T> T parseJson(String json, Class<T> type) {
		try {
			log.info("posting json="+json);
			return mapper.readValue(json, type);
		} catch (JsonParseException e) {
			log.warn("There was a problem parsing the JSON request.", e);
			throw new BadRequest("There was a problem parsing the JSON request.");
		} catch (JsonMappingException e) {
			log.warn("Your json request appears to be invalid.", e);
			throw new BadRequest("Your json request appears to be invalid.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
