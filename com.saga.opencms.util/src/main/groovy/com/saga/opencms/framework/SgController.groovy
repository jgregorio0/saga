package com.saga.opencms.framework

import org.apache.commons.logging.Log
import org.opencms.json.JSONObject
import org.opencms.jsp.CmsJspActionElement
import org.opencms.main.CmsLog

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.jsp.PageContext

class SgController extends CmsJspActionElement {

    static final Log LOG = CmsLog.getLog(SgController.class)

    def b_pageContext;
    def b_request;
    def b_response;

    SgController() {}

    def main(String[] args) {
        new SgController(b_pageContext, b_request, b_response).handler()
    }

    SgController(PageContext context, HttpServletRequest req, HttpServletResponse res) {
        super(context, req, res)
    }

    void handler() {
        JSONObject jRes = null;
        try {
            load(request);
            if (validate()) {
                jRes = execute();
            }
        } catch (
                Exception e
                ) {
            LOG.error("consulta " + SQL, e);
        } finally {
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jRes.toString());
        }
    }
}
