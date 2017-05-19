package com.saga.opencms.util

import com.saga.sagasuite.scripts.SgReportManager
import org.opencms.main.CmsException
import org.opencms.main.CmsMultiException

public class SgLog {

	static final String sepHTML = "<br/>";
	static final String sepTXT = "\n";
	static final String colorOk = "green";
	static final String colorWarn = "olive";
	static final String colorError = "maroon";
	String msg;
	String color;
	String sep;

	def infos;
	def warns;
	def errors;

	SgReportManager reportManager;
	def user;
	def porcentaje;
	def idProceso;

	/**
	 * Initialize content. The resource must be locked by user before.
	 * @param cmso
	 * @param resourcePath
	 * @param locale
	 */

	public SgLog(def reportManager, def idProceso, def user) {
		this.reportManager = reportManager
		this.idProceso = idProceso
		this.user = user
		this.porcentaje = new Double(0)

		init()
	}

	def init(){
		this.msg = ""
		this.color = colorOk;
		this.sep = sepTXT + sepHTML;
		this
	}

	def warn() {
		if (!color.equals(colorError)){
			color = colorWarn
		}
		this
	}

	def warn(String msg) {
		warn().add(msg)
	}

	def warn(Exception e) {
		warn("<strong>${e.getMessage()}</strong><br/>"+
				"Cause: ${e.getCause()}<br/>"+
				CmsException.getStackTraceAsString(e))
		this
	}

	def warn(Object o, Exception e) {
		add(o)
		warn("<strong>${e.getMessage()}</strong><br/>"+
				"Cause: ${e.getCause()}<br/>"+
				CmsException.getStackTraceAsString(e));
		this
	}

	def error() {
		color = colorError
		this
	}

	def error(String msg) {
		error().add(msg)
	}

	def error(Exception e) {
		error("<strong>${e.getMessage()}</strong><br/>"+
				"Cause: ${e.getCause()}<br/>"+
				CmsException.getStackTraceAsString(e))
		this
	}
	def error(Object o, Exception e) {
		add(o)
		error("<strong>${e.getMessage()}</strong><br/>"+
				"Cause: ${e.getCause()}<br/>"+
				CmsException.getStackTraceAsString(e));
		this
	}

	def print() {
		reportManager.addMessage(
				idProceso,
				user,
				"<div style='color:${color};'>${msg}</div>".toString(),
				porcentaje);
		cleanMsg();
		this;
	}

	def cleanMsg(){
		msg = "";
		this
	}

	def add(Object msg) {
		this.msg = this.msg + sep + msg.toString()
		this
	}

	def percentage(def i, def total){
		porcentaje = (new Double(i) * 100d) / total;
		this
	}

	def loop(def i, def total){
		init();
		percentage(i, total);
		this;
	}

	/**
	 * Returns the stack trace (including the message) of an exception as a String.<p>
	 *
	 * If the exception is a CmsException,
	 * also writes the root cause to the String.<p>
	 *
	 * @param e the exception to get the stack trace from
	 * @return the stack trace of an exception as a String
	 */
	public static String getStackTraceAsString(Throwable e) {

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		if (e instanceof CmsMultiException) {
			CmsMultiException me = (CmsMultiException)e;
			Iterator<CmsException> it = me.getExceptions().iterator();
			while (it.hasNext()) {
				Throwable t = it.next();
				t.printStackTrace(printWriter);
			}
		}
		return stringWriter.toString();
	}
}