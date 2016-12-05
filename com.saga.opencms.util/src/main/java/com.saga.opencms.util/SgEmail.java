package com.saga.opencms.util;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.apache.commons.mail.*;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * Created by jgregorio on 07/05/2015.
 */
public class SgEmail {

    private String host;
    private Integer port;
    private String base;
    private DefaultAuthenticator auth;
    private boolean isSSL;
    private String encoding;

    public SgEmail() {
    }

    public SgEmail(String host, Integer port, String base,
           String user, String pass, boolean isSSL, String encoding) {
        setHost(host);
        setPort(port);
        setAuth(new DefaultAuthenticator(user, pass));
        setSSL(isSSL);
        setEncoding(encoding);
    }

    private void initEmail(Email email) {
        email.setHostName(getHost());
        email.setSmtpPort(getPort());
        email.setAuthenticator(getAuth());
//        email.setSSLOnConnect(isSSL);
        email.setCharset(encoding);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public DefaultAuthenticator getAuth() {
        return auth;
    }

    public void setAuth(DefaultAuthenticator auth) {
        this.auth = auth;
    }

    public boolean isSSL() {
        return isSSL;
    }

    public void setSSL(boolean SSL) {
        isSSL = SSL;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void sendHtmlEmail(String from, List<String> to, String subject, String html) throws EmailException {
        HtmlEmail htmlEmail = new HtmlEmail();
        initEmail(htmlEmail);
        htmlEmail.setSSL(isSSL());
        htmlEmail.setFrom(from);
        htmlEmail.setSubject(subject);
        for (int i = 0; i < to.size(); i++) {
            htmlEmail.addTo(to.get(i));
        }
        htmlEmail.setHtmlMsg(html);
        htmlEmail.send();
    }

    public void sendSimpleMail(String from, String to, String subject, String text)
            throws EmailException {
        SimpleEmail email = new SimpleEmail();
        initEmail(email);
        email.setFrom(from);
        email.setSubject(subject);
        email.setContent(text, "text/html");
        email.addTo(to);
        email.send();
    }

    public void sendPebbleTemplateMail(
            String from, List<String> to, String subject,
            String template, Map<String, Object> context)
                throws IOException, PebbleException, EmailException {
        String htmlContent = initPebbleTemplate(template, context);
        sendHtmlEmail(from, to, subject, htmlContent);
    }

    private String initPebbleTemplate(String template, Map<String, Object> context)
            throws IOException, PebbleException {
        context.put("base", base);
        return new SgPebble().process(template, context);
    }

}
