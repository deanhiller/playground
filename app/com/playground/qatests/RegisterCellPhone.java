package com.playground.qatests;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterCellPhone {
	
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println("Enter the cell phone that will register right now with the server");

		Scanner s = new Scanner(System.in);
		String phoneNumber = s.nextLine();
		
		DefaultHttpClient httpclient = new DefaultHttpClient();

		String registerKey = "AAAAB3NzaC1kc3MAAACBAI0XYYyhYT861agRCv2BCIg6HjgARc3GnbmuXGkbrXzACzZAy1uQ6wteRDZpByiPVJaL8DKncf91QoFIBZKJ0ao7ZuOiCQ03VUfxb6YwMXMeLikjcSI+zRBh6NPP833mtYVpLG1kDpGGxmJdmt38iWvxqa9HJcLOzYQA6lqyPAAAAFQDUa2rnAC9arD905h42VwI2da+tawAAAIEAhzarb59ddJWTW831YZorBrpKPZp+WWAmO+4rjp82owQsI9aua4qfcSenb4+U";
		String key = Utility.getData(registerKey, phoneNumber, httpclient);

		System.out.println("The key to register your cell on the website displayed on the mobile app is="+key);
	}
}
