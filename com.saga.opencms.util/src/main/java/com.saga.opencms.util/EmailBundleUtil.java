package com.saga.opencms.util;


import com.sun.xml.messaging.saaj.packaging.mime.internet.MimeUtility;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.mail.EmailException;
import org.opencms.i18n.CmsResourceBundleLoader;
import org.opencms.mail.CmsHtmlMail;
import org.opencms.main.CmsLog;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

public class EmailBundleUtil {

    private static final Log LOG = CmsLog.getLog(EmailBundleUtil.class);

    private ResourceBundle bundle;
    private String fromKey;
    private String from;
    private String toKey;
    private String to;
    private String subjectKey;
    private String subject;

    public EmailBundleUtil(String bundleBaseName, Locale locale, String fromKey, String toKey, String subjectKey) {
        bundle = CmsResourceBundleLoader.getBundle(bundleBaseName, locale);
        this.fromKey = fromKey;
        this.toKey = toKey;
        this.subjectKey = subjectKey;
        initMsgs();
    }

    private void initMsgs() {
        from = bundle.getString(fromKey);
        to = bundle.getString(toKey);
        subject = bundle.getString(subjectKey);
    }

    public void sendMail(String html) throws EmailException {
        if (!StringUtils.isEmpty(html)) {
            MailService mail = new MailService(html, from, to, subject);
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
