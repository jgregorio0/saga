package com.saga.opencms.util

import org.apache.commons.logging.Log
import org.apache.http.HttpEntity
import org.apache.http.NameValuePair
import org.apache.http.auth.AuthenticationException
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.*
import org.apache.http.entity.StringEntity
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.opencms.main.CmsLog

import java.nio.charset.Charset

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
    BasicCookieStore cookieStore;
    RequestConfig requestConfig;

    Exception exception;
    int resCode;
    String resStr;

    public SgNet(){
    }

    /**
     *
     * @param timeout milliseconds
     */
    public SgNet(int timeout){
        this.requestConfig = initRequestConfig(timeout);
    }

    /**
     * Init class
     * @param timeout miliseconds
     * @param cookieStore
     */
    public SgNet(int timeout, BasicCookieStore cookieStore){
        this(timeout);
        this.cookieStore = cookieStore;
    }

    /**
     * Init request config using timeout
     * @param timeout milliseconds
     * @return
     */
    public RequestConfig initRequestConfig(Integer timeout){
        if (timeout == null) {
            return null;
        } else {
            return RequestConfig.custom()
                    .setConnectTimeout(timeout)
                    .setSocketTimeout(timeout)
                    .setConnectionRequestTimeout(timeout)
                    .build();
        }
    }

    /**
     * Init client using requestConfig and cookieStore if defined.
     * Otherwise init default client.
     */
    private void initClient(){
        LOG.debug("init client - reqConf: " + (requestConfig != null) + " cookies: " + (cookieStore != null));
        if (requestConfig != null) {
            if (cookieStore != null) {
                client = HttpClients.custom()
                        .setDefaultRequestConfig(requestConfig)
                        .setDefaultCookieStore(cookieStore)
                        .build();
            } else {
                client = HttpClients.custom()
                        .setDefaultRequestConfig(requestConfig)
                        .build();
            }
        } else if (cookieStore != null) {
            client = HttpClients.custom()
                    .setDefaultCookieStore(cookieStore)
                    .build();
        } else {
            client = HttpClients.createDefault();
        }
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

    /**
     * List params as BasicNameValuePair
     * @param params
     * @return
     */
    private List<NameValuePair> listParams(Map<String, String> params) {
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
        initClient();

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

    /**
     * Execute post with authorizated header
     * @param url
     * @param jsonStr
     * @param user
     * @param pass
     * @throws IOException
     * @throws AuthenticationException
     */
    public void postAuth(String url, String jsonStr, String user, String pass)
            throws IOException, AuthenticationException {
        initClient();

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

    /**
     * Execute get to url
     * @param url
     * @param charset
     * @param close
     */
    public void get(String url, String charset, boolean close) {
        resCode = null;
        resStr = null;
        try {
            if (close) {
                initClient()
            }
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse res = client.execute(httpGet);
            resCode = res.getStatusLine().getStatusCode();
            HttpEntity resEntity = res.getEntity();
            if (resEntity != null)
                resStr = EntityUtils.toString(resEntity, charset);
            LOG.debug("check service " + url + " (" + resCode + "): " + resStr);

        } catch (Exception e) {
            LOG.error(e)
            exception = e;
        } finally {
            if (close && client != null) {
                client.close();
            }
        }

    }

    public void getAuth(String url, String user, String pass)
            throws IOException, AuthenticationException {
        initClient();
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

    /**
     * Execute put
     * @param url
     * @param jsonStr
     * @throws IOException
     */
    public void put(String url, String jsonStr) throws IOException {
        initClient();

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
        initClient();

        HttpDelete httpDelete = new HttpDelete(url);

        CloseableHttpResponse response = client.execute(httpDelete);
        resCode = response.getStatusLine().getStatusCode();
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null)
            resStr = EntityUtils.toString(resEntity);
        client.close();
    }
}