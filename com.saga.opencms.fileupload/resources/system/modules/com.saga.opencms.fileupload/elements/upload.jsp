<%@ page import="com.saga.opencms.fileupload.FileUpload" %>
<%@ page import="groovy.lang.GroovyClassLoader" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.io.File" %>
<%@ page import="groovy.lang.GroovyObject" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%
	// Configurate parameters
	String folder = "/.galleries/uploads";
	String size = "3MB";
	String format = "pdf|png";

	pageContext.setAttribute("folder", folder);
	pageContext.setAttribute("size", size);
	pageContext.setAttribute("format", format);
%>


<%--
	FileUpload fileupload = new FileUpload(pageContext, request, response);
	fileupload.handleRequest();
--%>

<%
	ClassLoader parent = getClass().getClassLoader();
	GroovyClassLoader loader = new GroovyClassLoader(parent);

	//Obtenemos la ruta del webinf:
	String webInfPath = OpenCms.getSystemInfo().getWebInfRfsPath();
	String scriptName = "classes/com/saga/opencms/fileupload/Upload.groovy";

	//Obtiene la clase groovy
	Class groovyClass = loader.parseClass(new File(webInfPath+scriptName));

	GroovyObject groovyObject = (GroovyObject) groovyClass.newInstance();
	groovyObject.invokeMethod("init", new Object[] {pageContext, request, response});
%>

