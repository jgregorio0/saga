<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>



<%@page import="groovy.lang.Binding"%>
<%@page import="groovy.util.GroovyScriptEngine"%>
<%@page import="org.opencms.util.CmsStringUtil"%>
<%@page import="org.opencms.main.OpenCms"%>
<%@page import="org.opencms.main.CmsLog"%>
<%@page import="org.apache.commons.logging.Log"%>
<%
	Log LOG = CmsLog.getLog(this.getClass());

	String classesPath = "/classes";
	String modulePath = "/com/saga/cedinox/suscriptores";
	String scriptName = "controller/SuscriptoresRead.groovy";
	String dirClasses = OpenCms.getSystemInfo().getWebInfRfsPath() + classesPath;
	String rfsClassesModule = CmsStringUtil.joinPaths(dirClasses, modulePath);

	GroovyScriptEngine engine = new GroovyScriptEngine(rfsClassesModule);
	Binding binding = new Binding();
	binding.setProperty("b_pageContext", pageContext);
	binding.setProperty("b_request", request);
	binding.setProperty("b_response", response);
	binding.setProperty("b_engine", engine);
	try {
		engine.run(scriptName, binding);
	} catch (Exception e) {
		LOG.error(e, e);
	}
%>

<%--
<%@ page import="com.saga.cedinox.suscriptores.controller.SuscriptoresRead"%>
<%
	new SuscriptoresRead(pageContext, request, response).handler();
%>
--%>