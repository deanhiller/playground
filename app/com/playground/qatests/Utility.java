package com.playground.qatests;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import models.message.NewKeyResponse;
import models.message.PutTextMessage;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Utility {

	private final static Logger log = LoggerFactory.getLogger(Utility.class);
	
	@SuppressWarnings("rawtypes")
	public
	static String getData(String key, String phone, DefaultHttpClient httpClient) throws ClientProtocolException, IOException {
		String requestUri = "/api/keyrequest/"+phone+"/"+key;
		String theString = Utility.sendRequest(httpClient, requestUri, null);
		
		log.info("strResp="+theString);
		ObjectMapper mapper = new ObjectMapper();
		NewKeyResponse keyResponse = mapper.readValue(theString, NewKeyResponse.class);
		
		log.info("response="+keyResponse);
		return keyResponse.getKey();
	}
	
	static BasicHttpContext setupPreEmptiveBasicAuth(HttpHost targetHost,
			DefaultHttpClient httpclient, String getKey) {
		httpclient.getCredentialsProvider().setCredentials(
		        new AuthScope(targetHost.getHostName(), targetHost.getPort()), 
		        new UsernamePasswordCredentials("program", getKey));
	
		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);
	
		// Add AuthCache to the execution context
		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
		return localcontext;
	}

	public static String sendRequest(DefaultHttpClient httpclient, String requestUri, String password) throws IOException,
			ClientProtocolException {
		HttpHost targetHost = new HttpHost("localhost", 9000, "http"); 
		//BasicHttpContext localcontext = setupPreEmptiveBasicAuth(targetHost, httpclient, getKey);
		
		log.info("trying to hit url="+requestUri);
		HttpGet httpget = new HttpGet(requestUri);
		//HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
		HttpResponse response = httpclient.execute(targetHost, httpget);
		HttpEntity entity = response.getEntity();
		
		InputStream instream = entity.getContent();
		StringWriter writer = new StringWriter();
		IOUtils.copy(instream, writer);
		String theString = writer.toString();

		log.info("json response="+theString);
		return theString;
	}

	public static String sendPostRequest(DefaultHttpClient httpclient, String requestUri, String body) {
		try {
			return sendPostRequestImpl(httpclient, requestUri, body);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String sendPostRequestImpl(DefaultHttpClient httpclient, String requestUri, String body) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(requestUri);
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.setEntity(new StringEntity(body));
		HttpResponse response2 = httpclient.execute(httpPost);

		//read out the body so we can re-use the client
		HttpEntity entity = response2.getEntity();
		InputStream in = entity.getContent();
		StringWriter writer = new StringWriter();
		IOUtils.copy(in, writer);
		String theString = writer.toString();
		
		log.info("resp="+response2.getStatusLine()+" body="+theString);
		Assert.assertEquals(200, response2.getStatusLine().getStatusCode());
		
		return theString;
	}
	
	public static void postMessage(DefaultHttpClient httpclient, String cellNum, 
			String remoteNum, String txtMsg, String key, long time, boolean isOutgoing) throws UnsupportedEncodingException,
			IOException, ClientProtocolException {
		
		PutTextMessage msg = new PutTextMessage();
		msg.setKey(key);
		msg.setPhoneTime(time);
		msg.setRemoteNumber(remoteNum);
		msg.setTextMessage(txtMsg);
		msg.setOutgoing(isOutgoing);
		
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(msg);
		Utility.sendPostRequest(httpclient, "http://localhost:9000/api/postdata", json);
	}
}
