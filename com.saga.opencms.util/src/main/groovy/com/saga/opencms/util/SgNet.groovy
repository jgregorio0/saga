package com.saga.opencms.util

import org.apache.http.HttpEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.utils.URIBuilder
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.apache.ivy.util.url.BasicURLHandler

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class SgNet {

    String url;
    def params;


    public SgNet(String url){
        this.url = url
        params = [:]
    }

    String httpGetResponse(){
        final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        final URIBuilder uriBuilder = new URIBuilder(url)
        addParams(uriBuilder)
        final HttpGet httpGet = new HttpGet(uriBuilder.build());
        final CloseableHttpResponse execute = httpClient.execute(httpGet);
        if (execute.getStatusLine().getStatusCode() == BasicURLHandler.HttpStatus.SC_OK) {
            try {
                final HttpEntity entity = execute.getEntity();
                return EntityUtils.toString(entity, "UTF-8");
            } catch (Exception e) {
                throw e;
            } finally {
                execute.close();
            }
        }
        return null
    }

    def addParams(URIBuilder uriBuilder) {
        def nvps = []
        params.each {
            nvps.add(new BasicNameValuePair(it.key, it.value))
        }
        uriBuilder.addParameters(nvps)
    }

    String httpsResponse() {
        URL myurl = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
        InputStream ins = con.getInputStream();
        InputStreamReader isr = new InputStreamReader(ins);
        BufferedReader br = new BufferedReader(isr);
        String input = "";
        try {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                input = input + inputLine;
            }


        }catch(IOException e){
            throw e;
        } finally {
            br.close();
        }
    }

    public String https(double amount,double price){
        // Create a trust manager that does not validate certificate chains
        X509TrustManager trMan = new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        };
        TrustManager[] trustAllCerts = [trMan].toArray();
        // Install the all-trusting trust manager
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        final URIBuilder uriBuilder = new URIBuilder(url)
        addParams(uriBuilder)
        URL url = new URL(uriBuilder.build());
        URLConnection con = url.openConnection();
        final Reader reader = new InputStreamReader(con.getInputStream());
        final BufferedReader br = new BufferedReader(reader);
        String input = "";
        String line = "";
        while ((line = br.readLine()) != null) {
            input = input + line;
        }
        br.close();

        return input;
    }
}