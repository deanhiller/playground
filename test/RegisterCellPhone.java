
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.playground.qatests.Utility;

public class RegisterCellPhone {
	
	@Test
	public void registerPostAndGet() throws JsonGenerationException, JsonMappingException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		
		String registerKey = "AAAAB3NzaC1kc3MAAACBAI0XYYyhYT861agRCv2BCIg6HjgARc3GnbmuXGkbrXzACzZAy1uQ6wteRDZpByiPVJaL8DKncf91QoFIBZKJ0ao7ZuOiCQ03VUfxb6YwMXMeLikjcSI+zRBh6NPP833mtYVpLG1kDpGGxmJdmt38iWvxqa9HJcLOzYQA6lqyPAAAAFQDUa2rnAC9arD905h42VwI2da+tawAAAIEAhzarb59ddJWTW831YZorBrpKPZp+WWAmO+4rjp82owQsI9aua4qfcSenb4+U";
		String key = Utility.getData(registerKey, "3034445555", httpclient);
		String key2 = Utility.getData(registerKey, "3032228888", httpclient);

		Utility.postMessage(httpclient, "5678", "8765", "hel there 1", key, time(1));
		Utility.postMessage(httpclient, "1234", "4321", "hi there buddy", key, time(-1));

		Utility.postMessage(httpclient, "5555", "6666", "555 to 666", key2, time(2));
		Utility.postMessage(httpclient, "4444", "3333", "444 to 3333", key2, time(3));
	}

	private long time(int days) {
		DateTime t = new DateTime();
		DateTime newTime = t.plusDays(days);
		return newTime.getMillis();
	}
}
