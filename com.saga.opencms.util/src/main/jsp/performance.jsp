<%@ page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%
    long start = System.currentTimeMillis();
%>
<%--CODE--%>
<p style="color:red;background:white"><%=new Double((System.currentTimeMillis() - start)).toString()%> ms</p>