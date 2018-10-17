<%@ page import="org.opencms.main.OpenCms" %>
<%@page buffer="none" session="false"%>
<div>
	<p>Java version: <%= System.getProperty("java.version")%></p>
	<p>Tomcat version: <%= application.getServerInfo() %></p>
	<p>OpenCms version: <%= OpenCms.getSystemInfo().getVersion() %></p>
</div>