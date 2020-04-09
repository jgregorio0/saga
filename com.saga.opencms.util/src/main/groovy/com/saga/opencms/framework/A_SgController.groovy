package com.saga.opencms.framework

import org.apache.commons.logging.Log
import org.opencms.jsp.CmsJspActionElement
import org.opencms.main.CmsLog

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.jsp.PageContext

abstract class A_SgController extends CmsJspActionElement {

	private static final Log LOG = CmsLog.getLog(A_SgController.class)

	public static final String PARAM_CTXT = "ctxt"

	Map ctxt

	A_SgController() {
	}

	A_SgController(PageContext context, HttpServletRequest req, HttpServletResponse res) {
		super(context, req, res)
	}

	public Map getCtxt() {
		return ctxt;
	}

	public void setCtxt(Map ctxt) {
		this.ctxt = ctxt;
	}

	void handler() {
		ctxt = [:];
		try {
			load();
			if (validate()) {
				execute();
			}
		} catch (Exception e) {
			error(e)
		} finally {
			save()
		}
	}

	abstract void load();

	boolean validate() {
		return true;
	}

	abstract void execute();

	void error(Exception e) {
		LOG.error(e, e)
	}

	void save() {
		getJspContext().setAttribute(PARAM_CTXT, ctxt)
	}
}
