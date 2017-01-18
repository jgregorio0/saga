package com.saga.sagasuite.padron.util;

import org.apache.commons.logging.Log;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.opencms.main.CmsLog;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jgregorio on 09/09/2016.
 * Ref: http://www.baeldung.com/httpclient-post-http-request
 *
 * Utility class for request using HTTP GET and POST methods
 */
public class SgNet {
	private static final Log LOG = CmsLog.getLog(SgNet.class);

	public static String charset = "UTF-8";
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String H_USER_AGENT = "User-Agent";
	public static final String H_MOZILLA = "Mozilla/5.0";
	public static final String H_CONTENT_TYPE = "Content-type";
	public static final String H_ACCEPT = "Accept";
	public static final String H_APP_JSON = "application/json";

	CloseableHttpClient client;

	Map<String, String> params;
	Map<String, String> headers;

	int resCode;
	String resStr;

	public SgNet(){
		headers = new HashMap<String, String>();
		params = new HashMap<String, String>();
		client = HttpClients.createDefault();
	}

	public SgNet(RequestConfig config){
		headers = new HashMap<String, String>();
		params = new HashMap<String, String>();
		client = HttpClients.custom()
				.setDefaultRequestConfig(config)
				.build();
	}

	public SgNet(int timeout){
		this(RequestConfig.custom()
				.setConnectTimeout(timeout)
				.setSocketTimeout(timeout)
				.setConnectionRequestTimeout(timeout)
				.build());
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public int getResCode() {
		return resCode;
	}

	public void setResCode(int resCode) {
		this.resCode = resCode;
	}

	public String getResStr() {
		return resStr;
	}

	public void setResStr(String resStr) {
		this.resStr = resStr;
	}

	private List<NameValuePair> listParams() {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		for (String key : params.keySet()) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		return nvps;
	}

	/**
	 * Post request to url adding json parameter as String
	 * @param url
	 * @param jsonStr
	 * @throws java.io.IOException
	 */
	public void post(String url, String jsonStr) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		StringEntity entity = new StringEntity(jsonStr, Charset.forName(charset));
		httpPost.setEntity(entity);
		httpPost.setHeader(H_ACCEPT, H_APP_JSON);
		httpPost.setHeader(H_CONTENT_TYPE, H_APP_JSON);

		CloseableHttpResponse response = client.execute(httpPost);
		resCode = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null)
			resStr = EntityUtils.toString(resEntity);
		client.close();
	}

	public void get(String url) throws IOException {
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse res = client.execute(httpGet);
		resCode = res.getStatusLine().getStatusCode();
		HttpEntity resEntity = res.getEntity();
		if (resEntity != null)
			resStr = EntityUtils.toString(resEntity);
		client.close();
	}

	public void postAuth(String url, String jsonStr, String user, String pass)
			throws IOException, AuthenticationException {
		HttpPost httpPost = new HttpPost(url);

		StringEntity entity = new StringEntity(jsonStr, Charset.forName(charset));
		httpPost.setEntity(entity);
		httpPost.setHeader(H_ACCEPT, H_APP_JSON);
		httpPost.setHeader(H_CONTENT_TYPE, H_APP_JSON);

		UsernamePasswordCredentials creds =
				new UsernamePasswordCredentials(user, pass);
		httpPost.addHeader(new BasicScheme().authenticate(creds, httpPost, null));

		CloseableHttpResponse response = client.execute(httpPost);
		resCode = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null)
			resStr = EntityUtils.toString(resEntity);
		client.close();
	}

	public void getAuth(String url, String user, String pass)
			throws IOException, AuthenticationException {
		HttpGet httpGet = new HttpGet(url);

		UsernamePasswordCredentials creds =
				new UsernamePasswordCredentials(user, pass);
		httpGet.addHeader(new BasicScheme().authenticate(creds, httpGet, null));

		CloseableHttpResponse response = client.execute(httpGet);
		resCode = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null)
			resStr = EntityUtils.toString(resEntity);
		client.close();
	}

	public void put(String url, String jsonStr) throws IOException {
		HttpPut httpPut = new HttpPut(url);

		StringEntity entity = new StringEntity(jsonStr, Charset.forName(charset));
		httpPut.setEntity(entity);
		httpPut.setHeader(H_ACCEPT, H_APP_JSON);
		httpPut.setHeader(H_CONTENT_TYPE, H_APP_JSON);

		CloseableHttpResponse response = client.execute(httpPut);
		resCode = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null)
			resStr = EntityUtils.toString(resEntity);
		client.close();
	}

	public void put(String url) throws IOException {
		HttpPut httpPut = new HttpPut(url);

		CloseableHttpResponse response = client.execute(httpPut);
		resCode = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null)
			resStr = EntityUtils.toString(resEntity);
		client.close();
	}

	/**
	 * Delete request to url
	 * @param url
	 * @throws java.io.IOException
	 */
	public void delete(String url) throws IOException {
		HttpDelete httpDelete = new HttpDelete(url);

		CloseableHttpResponse response = client.execute(httpDelete);
		resCode = response.getStatusLine().getStatusCode();
		HttpEntity resEntity = response.getEntity();
		if (resEntity != null)
			resStr = EntityUtils.toString(resEntity);
		client.close();
	}
}