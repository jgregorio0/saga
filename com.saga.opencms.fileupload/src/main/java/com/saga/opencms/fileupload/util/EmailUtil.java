package com.saga.opencms.fileupload.util;

import org.apache.commons.logging.Log;
import org.apache.commons.mail.EmailException;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.main.CmsLog;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;



public class EmailUtil {

	private static final Log LOG = CmsLog.getLog(EmailGenerator.class);

	private String subject;
	private String from;
	private String to;
	private String errorsTitle;
	private String modifiesTitle;
	private String deletesTitle;
	private List<String> errors;
	private List<String> modifies;
	private List<String> deletes;

	public EmailUtil(String subject, String from, String to,
							   String erroresT, String modifiesT,
							   String deletesT) {
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.errorsTitle = erroresT;
		this.modifiesTitle = modifiesT;
		this.deletesTitle = deletesT;
		this.errors = new ArrayList<String>();
		this.modifies = new ArrayList<String>();
		this.deletes = new ArrayList<String>();
	}

	public void infoError(String error) {
		errors.add(error);
	}

	public void infoError(String error, Exception e) {
		errors.add(error);
		errors.add("<code>" + e.getCause().toString() + "</code>");
	}

	public void infoErrors(String msg, List<?> objects) {
		addMsg(errors, msg, objects);
	}

	public void infoModifies(String msg, List<?> objects) {
		addMsg(modifies, msg, objects);
	}

	public void infoDeletes(String msg, List<?> objects) {
		addMsg(deletes, msg, objects);
	}

	private void addMsg(List<String> list, String msg, List<?> objects) {
		String mensaje = null;
		if (!objects.isEmpty()) {
			mensaje = msg != null ? "<p>" + msg + "</p>" : "";
			mensaje += "<ol>";
			for (Object o : objects) {
				mensaje += "<li>" + o.toString() + "</li>";
			}
			mensaje += "</ol>";
		}
		if (mensaje != null) {
			list.add(mensaje);
		}
	}

	public void sendMail(){
		String html = "";
		if (!errors.isEmpty()){
			html += "<h1>" + errorsTitle + "</h1>";
			for (String s : errors){
				html += "<p>" + s + "</p>";
			}
			html += "</br>";
		}
		if (!modifies.isEmpty()) {
			html += "<h1>" + modifiesTitle + "</h1>";
			for (String s : modifies){
				html += "<p>" + s + "</p>";
			}
			html += "</br>";
		}
		if (!deletes.isEmpty()) {
			html += "<h1>" + deletesTitle + "</h1>";
			for (String s : deletes){
				html += "<p>" + s + "</p>";
			}
			html += "</br>";
		}
		if (!html.equals("")) {
			try {
				EmailGenerator email = new EmailGenerator(html, from, to, subject);
				email.run();
			} catch (EmailException e) {
				LOG.error("ERROR Enviando el email con asunto: " +
						subject + " de: " +
						from + " a: " + to, e);
			}
		}
	}

	public class EmailGenerator extends Thread {

		private CmsHtmlMail mail;
		private boolean send = false;

		public EmailGenerator(String html, String from, String to, String subject) throws EmailException {
			mail = new CmsHtmlMail();
			mail.setCharset("UTF-8");
			mail.setHtmlMsg(html);
			String[] emails=to.split(";");
			for(int i=0;i<emails.length && i<5;i++){
				if(emails[i]!=null && emails[i].trim().length()>0 && emails[i].contains("@")){
					mail.addBcc(emails[i]);
					send=true;
				}
			}
			mail.setFrom(from);
			mail.setSubject(subject);
		}

		public EmailGenerator(String html, String from, String to, String subject, String encoding) throws EmailException {
			mail = new CmsHtmlMail();
			mail.setCharset(encoding);

			mail.setHtmlMsg(html);
			String[] emails=to.split(";");
			for(int i=0;i<emails.length && i<5;i++){
				if(emails[i]!=null && emails[i].trim().length()>0 && emails[i].contains("@")){
					mail.addBcc(emails[i]);
					send=true;
				}
			}
			mail.setFrom(from);
			try {
				mail.setSubject(MimeUtility.encodeText(subject, encoding, "Q"));
			} catch (UnsupportedEncodingException e) {
				mail.setSubject(subject);
			}
		}

		@Override
		public void run() {
			if(send){
				try {
					mail.send();
				} catch (EmailException e) {
					LOG.error(e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
