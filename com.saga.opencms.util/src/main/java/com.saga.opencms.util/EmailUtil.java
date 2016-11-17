package com.saga.opencms.util;

import org.apache.commons.logging.Log;
import org.apache.commons.mail.EmailException;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.main.CmsLog;

import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EmailUtil {

    private static final Log LOG = CmsLog.getLog(EmailUtil.class);

    private String html;
    private String from;
    private String to;
    private String asunto;
    private String erroresT;
    private String modificacionesT;
    private String eliminadosT;
    private List<String> errores;
    private List<String> modificaciones;
    private List<String> eliminados;

    public EmailUtil(String asunto, String from,
                     String to, String erroresT,
                     String modificacionesT, String eliminadosT) {
        this.asunto = asunto;
        this.from = from;
        this.to = to;
        this.html = "";
        this.erroresT = erroresT;
        this.modificacionesT = modificacionesT;
        this.eliminadosT = eliminadosT;
        errores = new ArrayList<String>();
        modificaciones = new ArrayList<String>();
        eliminados = new ArrayList<String>();
    }

    public void infoError(String error) {
        errores.add(error);
    }

    public void infoError(String error, Exception e) {
        errores.add(error);
        errores.add("<code>" + e.getCause().toString() + "</code>");
    }

    public void infoErrors(String msg, List<?> campos) {
        addMsg(errores, msg, campos);
    }

    public void infoModifies(String msg, List<?> campos) {
        addMsg(modificaciones, msg, campos);
    }

    public void infoDeletes(String msg, List<?> campos) {
        addMsg(eliminados, msg, campos);
    }

    private void addMsg(List<String> list, String msg, List<?> campos) {
        String mensaje = null;

        if (!campos.isEmpty()) {
            mensaje = msg != null? msg : "";
            mensaje += "<ol>";
            for (Object s : campos) {
                mensaje += "<li>" + s.toString() + "</li>";
            }
            mensaje += "</ol>";
        }

        if (mensaje != null) {
            list.add(mensaje);
        }
    }

    public void sendMail() throws EmailException {
        html = "";

        if (!errores.isEmpty()) {
            html += "<h1>" + erroresT + "</h1>";
            for (String s : errores){
                html += "<p>" + s + "</p>";
            }
            html += "</br>";
        }

        if (!modificaciones.isEmpty()) {
            html += "<h1>" + modificacionesT + "</h1>";
            for (String s : modificaciones){
                html += "<p>" + s + "</p>";
            }
            html += "</br>";
        }

        if (!eliminados.isEmpty()) {
            html += "<h1>" + eliminadosT + "</h1>";
            for (String s : eliminados){
                html += "<p>" + s + "</p>";
            }
            html += "</br>";
        }

        if (!html.equals("")) {
            MailService mail = new MailService(html, from, to, asunto);
            mail.run();
        }
    }


    public class MailService extends Thread {

        private CmsHtmlMail mail;
        private boolean send = false;

        public MailService(String html, String from, String to, String asunto) throws EmailException {
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
            mail.setSubject(asunto);
        }

        public MailService(String html, String from, String to, String asunto, String encoding) throws EmailException {
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
                mail.setSubject(MimeUtility.encodeText(asunto, encoding, "Q"));
            } catch (UnsupportedEncodingException e) {
                mail.setSubject(asunto);
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
