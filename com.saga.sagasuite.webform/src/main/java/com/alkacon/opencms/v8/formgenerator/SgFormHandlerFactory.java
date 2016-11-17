//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.alkacon.opencms.v8.formgenerator;

import org.opencms.i18n.CmsMessages;
import org.opencms.util.CmsStringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

public final class SgFormHandlerFactory {
    public static final String ATTRIBUTE_ADDMESSAGE = "addMessage";
    public static final String ATTRIBUTE_FORMHANDLER = "cmsF";

    public static SgFormHandler create(PageContext context, HttpServletRequest req, HttpServletResponse res) throws Exception {
        return create(context, req, res, (String)null, (String)null);
    }

    public static SgFormHandler create(PageContext context, HttpServletRequest req, HttpServletResponse res, String formConfigUri) throws Exception {
        return create(context, req, res, (String)null, formConfigUri);
    }

    public static SgFormHandler create(PageContext context, HttpServletRequest req, HttpServletResponse res, String clazz, String formConfigUri) throws Exception {
        SgFormHandler formHandler = null;
        SgFormHandler formHandlerFromContext = null;
        if(context != null) {
            formHandlerFromContext = (SgFormHandler)context.getAttribute("cmsF");
        }

        if(formHandlerFromContext == null) {
            if(CmsStringUtil.isEmptyOrWhitespaceOnly(clazz)) {
                formHandler = new SgFormHandler();
            } else {
                formHandler = (SgFormHandler)Class.forName(clazz).newInstance();
            }

            if(context != null && req != null && res != null) {
                formHandler.init(context, req, res, formConfigUri);
            }
        } else {
            formHandler = formHandlerFromContext;
        }

        if(context != null && context.getAttribute("addMessage") != null) {
            formHandler.addMessages(new CmsMessages((String)context.getAttribute("addMessage"), formHandler.getRequestContext().getLocale()));
        }

        return formHandler;
    }

    private SgFormHandlerFactory() {
    }
}
