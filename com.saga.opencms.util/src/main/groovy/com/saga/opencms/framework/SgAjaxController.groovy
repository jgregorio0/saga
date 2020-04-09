package com.saga.opencms.framework

import org.apache.commons.logging.Log
import org.opencms.jsp.CmsJspActionElement
import org.opencms.main.CmsLog

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.jsp.PageContext

class SgAjaxController extends CmsJspActionElement {

    static final Log LOG = CmsLog.getLog(SgAjaxController.class)

    def b_pageContext;
    def b_request;
    def b_response;

    SgAjaxController() {}

    def main(String[] args) {
        new SgAjaxController(b_pageContext, b_request, b_response).handler()
    }

    SgAjaxController(PageContext context, HttpServletRequest req, HttpServletResponse res) {
        super(context, req, res)
    }
}
