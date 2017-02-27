<%@ page import="org.opencms.main.OpenCms" %>
<%@ taglib prefix="c" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<div>
	<p>Tomcat version: <%= application.getServerInfo() %></p>
	<p>OpenCms version: <%= OpenCms.getSystemInfo().getVersion() %></p>
</div>