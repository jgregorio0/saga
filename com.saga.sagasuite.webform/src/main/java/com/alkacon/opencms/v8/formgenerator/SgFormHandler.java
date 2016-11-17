/*
 * File   : $Source: /usr/local/cvs/alkacon/com.alkacon.opencms.v8.formgenerator/src/com/alkacon/opencms/v8/formgenerator/CmsFormHandler.java,v $
 * Date   : $Date: 2011-05-17 12:36:06 $
 * Version: $Revision: 1.25 $
 *
 * This file is part of the Alkacon OpenCms Add-On Module Package
 *
 * Copyright (c) 2010 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * The Alkacon OpenCms Add-On Module Package is free software: 
 * you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Alkacon OpenCms Add-On Module Package is distributed 
 * in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Alkacon OpenCms Add-On Module Package.  
 * If not, see http://www.gnu.org/licenses/.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com.
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org.
 */

package com.alkacon.opencms.v8.formgenerator;

import org.antlr.stringtemplate.StringTemplate;
import org.opencms.util.CmsRequestUtil;
import org.opencms.util.CmsStringUtil;

import javax.servlet.ServletException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.Map;

/**
 * The form handler controls the HTML or mail output of a configured email form.<p>
 * 
 * Provides methods to determine the action that takes place and methods to create different
 * output formats of a submitted form.<p>
 * 
 * @author Andreas Zahner 
 * @author Thomas Weckert
 * @author Jan Baudisch
 * 
 * @version $Revision: 1.25 $
 * 
 * @since 7.0.4 
 */
public class SgFormHandler extends CmsFormHandler {

    public SgFormHandler() {
        super();
    }

    @Override
    public void createForm() throws IOException, JspException, ServletException {
        JspWriter out = this.getJspContext().getOut();
        String modulePath = "/system/modules/" + CmsForm.MODULE_NAME + "/";
        String errorLinkTarget;
        StringTemplate sTemplate;
        if(this.showTemplate()) {
            out.write(this.getStyleSheet());
            errorLinkTarget = null;
            if(this.isInitSuccess() && this.getFormConfiguration().isRefreshSession()) {
                errorLinkTarget = this.link(modulePath + "resources/js/keepsession.js");
            }

            sTemplate = this.getOutputTemplate("form_js");
            sTemplate.setAttribute("formconfig", this.getFormConfiguration());
            sTemplate.setAttribute("sessionjs", errorLinkTarget);
            sTemplate.setAttribute("sessionuri", this.link(modulePath + "elements/keepsession.jsp"));
            sTemplate.setAttribute("subfieldjs", this.link(modulePath + "resources/js/subfields.js"));
            out.write(sTemplate.toString());
            out.write(this.buildTemplateGroupCheckHtml());
        }

        if(!this.isInitSuccess()) {
            StringTemplate errorLinkTarget1 = this.getOutputTemplate("initerror");
            errorLinkTarget1.setAttribute("headline", this.getMessages().key("form.init.error.headline"));
            errorLinkTarget1.setAttribute("text", this.getMessages().key("form.init.error.description"));
            out.write(errorLinkTarget1.toString());
        } else if(this.showRelease()) {
            out.write(this.getFormConfiguration().getReleaseText());
        } else if(this.showExpired()) {
            out.write(this.getFormConfiguration().getExpirationText());
        } else if(this.showMaximumSubmissions()) {
            if(this.getFormConfiguration().getMaximumSubmissionsText() != null) {
                out.write(this.getFormConfiguration().getMaximumSubmissionsText());
            }
        } else if(!this.showForm() && this.isValidFormaction()) {
            if(this.showCheck()) {
                out.write(this.buildCheckHtml());
            } else if(this.showDownloadData()) {
                Map errorLinkTarget2 = this.getParameterMap();
                errorLinkTarget2.put("uri", new String[]{this.getFormConfiguration().getConfigUri().toString()});
                this.include(modulePath + "elements/datadownload.jsp", (String)null, false, errorLinkTarget2);
            } else {
                errorLinkTarget = this.executeBeforeWebformAction();
                if(CmsStringUtil.isNotEmptyOrWhitespaceOnly(errorLinkTarget)) {
//                    this.getResponse().sendRedirect(this.link(errorLinkTarget));
//                    CmsRequestUtil.forwardRequest(this.link(errorLinkTarget), this.m_parameterMap, this.getRequest(), this.getResponse());
                    out.write(this.buildFormHtml());
                    return;
                }

                if(this.sendData()) {
                    if(this.getFormConfiguration().hasTargetUri()) {
                        if(this.getFormConfiguration().isForwardMode()) {
                            CmsRequestUtil.forwardRequest(this.link(this.getFormConfiguration().getTargetUri()), this.m_parameterMap, this.getRequest(), this.getResponse());
                        } else {
                            this.getResponse().sendRedirect(this.link(this.getFormConfiguration().getTargetUri()));
                        }
                    } else {
                        out.write(this.buildConfirmHtml());
                    }

                    this.executeAfterWebformAction();
                } else {
                    sTemplate = this.getOutputTemplate("emailerror");
                    sTemplate.setAttribute("headline", this.getMessages().key("form.error.mail.headline"));
                    sTemplate.setAttribute("text", this.getMessages().key("form.error.mail.text"));
                    sTemplate.setAttribute("error", this.getErrors().get("sendmail"));
                    out.write(sTemplate.toString());
                }
            }
        } else {
            out.write(this.buildFormHtml());
        }
    }
}
