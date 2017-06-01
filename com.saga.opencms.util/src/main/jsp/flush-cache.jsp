<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.monitor.CmsMemoryMonitor" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%
	String msg = "OK";
	try {
		OpenCms.getMemoryMonitor().flushCache(CmsMemoryMonitor.CacheType.USER);
	} catch (Exception e){
		msg = "ERROR: " + e.getMessage() + CmsException.getStackTraceAsString(e);
	}
%>
<div><%=msg%></div>