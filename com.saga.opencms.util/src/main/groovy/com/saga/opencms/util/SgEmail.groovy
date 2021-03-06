package com.saga.opencms.util

import com.mitchellbosecke.pebble.error.PebbleException
import org.apache.commons.mail.*
import org.opencms.file.CmsObject
import org.opencms.mail.CmsMailHost
import org.opencms.main.CmsException
import org.opencms.main.OpenCms
import org.opencms.site.CmsSite

import javax.annotation.Nullable;

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
		setBase(base)
	}



	public SgEmail(boolean isSSL, String encoding, String siteRoot) {
		CmsMailHost defaultMailHost = OpenCms.getSystemInfo().getMailSettings().getDefaultMailHost();
		String baseUrl = getBaseUrl(siteRoot);
		initSgEmail(defaultMailHost.getHostname(), defaultMailHost.getPort(), defaultMailHost.getUsername(), defaultMailHost.getPassword(), isSSL, encoding, baseUrl);
	}

	/**
	 * Find base url for siteRoot from the given path
	 * @param path
	 * @return
	 */
	public static String getBaseUrl(String path) {
		String urlBase = null;
		String siteRoot = path;
		CmsSite siteForSiteRoot = OpenCms.getSiteManager().getSiteForSiteRoot(siteRoot);

		if (siteForSiteRoot != null) {
			urlBase = siteForSiteRoot.getUrl();
		} else {
			int iLastSlash = siteRoot.lastIndexOf("/");
			if (iLastSlash > 0) {
				siteRoot = siteRoot.substring(0, iLastSlash);
				urlBase = getBaseUrl(siteRoot);
			}
		}

		return urlBase;
	}

	private void initSgEmail(String host, Integer port,
							 String user, String pass,
							 boolean isSSL, String encoding, String base){
		setHost(host);
		setPort(port);
		setAuth(new DefaultAuthenticator(user, pass));
		setSSL(isSSL);
		setEncoding(encoding);
		setBase(base)
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

	String getBase() {
		return base
	}

	void setBase(String base) {
		this.base = base
	}

	public void sendHtmlEmail(String from, List<String> to, String subject, String html) throws EmailException {
		sendHtmlEmail(from, to, null, subject, html);
	}

	public void sendHtmlEmail(String from, List<String> to, @Nullable List<String> bcc, String subject, String html) throws EmailException {
		if (to == null || to.isEmpty()) {
			throw new IllegalArgumentException("HtmlEmail must be sent to a recipient, thus 'to' must not be empty");
		}

		HtmlEmail htmlEmail = new HtmlEmail();
		initEmail(htmlEmail);
		htmlEmail.setSSL(isSSL());
		htmlEmail.setFrom(from);
		htmlEmail.setSubject(subject);

		// to
		for (int i = 0; i < to.size(); i++) {
			htmlEmail.addTo(to.get(i));
		}

		// bcc
		if (bcc != null) {
			for (int i = 0; i < bcc.size(); i++) {
				htmlEmail.addBcc(bcc.get(i));
			}
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


	/**
	 * Send email searching template in VFS
	 *
	 * @param cmso
	 * @param from
	 * @param to
	 * @param subject
	 * @param template
	 * @param context
	 * @throws IOException
	 * @throws EmailException
	 */
	public void sendMustacheTemplateMail(
			CmsObject cmso,
			String from, List<String> to, @Nullable List<String> bcc, String subject,
			String template, Map<String, Object> context)
			throws IOException, EmailException, CmsException {
		String htmlContent = initMustacheTemplate(cmso, template, context);
		sendHtmlEmail(from, to, bcc, subject, htmlContent);
	}

	/**
	 * Send email searching template in VFS
	 *
	 * @param cmso
	 * @param from
	 * @param to
	 * @param subject
	 * @param template
	 * @param context
	 * @throws IOException
	 * @throws EmailException
	 */
	public void sendOneMailForEachToMustacheTemplate(
			CmsObject cmso,
			String from, List<String> to, @Nullable List<String> bcc, String subject,
			String template, Map<String, Object> context)
			throws IOException, EmailException, CmsException {
		String htmlContent = initMustacheTemplate(cmso, template, context);
		for (String recipient : to) {
			ArrayList<String> recipients = new ArrayList<String>();
			recipients.add(recipient);
			sendHtmlEmail(from, recipients, bcc, subject, htmlContent);
		}
	}

	private String initMustacheTemplate(CmsObject cmso, String template, Map<String, Object> context)
			throws IOException, CmsException {
		context.put("base", base);
		return new SgMustache(cmso).process(template, context);
	}
}
