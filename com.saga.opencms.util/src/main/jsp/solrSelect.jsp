<%@ page import="org.opencms.main.OpenCmsSolrHandler" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%
    OpenCmsSolrHandler handler = new OpenCmsSolrHandler();
    handler.doGet(request, response);
%>