package com.saga.opencms.framework


import org.apache.commons.logging.Log
import org.opencms.json.JSONArray
import org.opencms.json.JSONObject
import org.opencms.main.CmsLog

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.jsp.PageContext

abstract class A_SgAjaxController extends A_SgController {

    private static final Log LOG = CmsLog.getLog(A_SgAjaxController.class)

    A_SgAjaxController() {
    }

    A_SgAjaxController(PageContext context, HttpServletRequest req, HttpServletResponse res) {
        super(context, req, res)
    }

    @Override
    void execute() {
        long total;
        JSONArray data;

        setCtxt(com.saga.cedinox.suscriptores.util.SgJsonRes.successJResponse(total, data).m_map)
    }

    @Override
    void error(Exception e) {
        LOG.error(e, e)
        setCtxt(com.saga.cedinox.suscriptores.util.SgJsonRes.errorJResponse(e).m_map)
    }

    @Override
    void save() {
        JSONObject jRes = null;
        try {
            jRes = new JSONObject(getCtxt())
        } catch (Exception e) {
            jRes = com.saga.cedinox.suscriptores.util.SgJsonRes.errorJResponse(e);
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jRes.toString());
    }
}
