<%@ page import="java.io.File" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.util.Arrays" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%

	try {
		out.println(OpenCms.getSystemInfo().getWebInfRfsPath()

	} catch (Exception e) {
		out.println("ERROR " + e);
	}
%>