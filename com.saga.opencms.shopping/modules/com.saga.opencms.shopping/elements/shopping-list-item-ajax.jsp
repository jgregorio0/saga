<%@ page import="org.opencms.json.JSONArray" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="for" uri="http://java.sun.com/jsp/jstl/core" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%
    JSONArray datas = new JSONArray();
    JSONObject data1 = new JSONObject(
            "{\"id\": \"83e0a814-8040-11e7-a65a-f53d2dda7236\"" +
                    ", \"title\": \"Hola que ase\"}");
    JSONObject data2 = new JSONObject(
            "{\"id\": \"87707853-81e8-11e7-b716-f53d2dda7236\"" +
                    ", \"title\": \"Affiuuu!!\"}");
    datas.put(data1);
    datas.put(data2);
%>
<c:set var="datas" value="<%=datas%>"></c:set>
<c:forEach var="data" items="${datas}">
    <cms:include file="%(link.strong:/system/modules/com.jgregorio.opencms.fileupload/elements/shopping-list-item.jsp)">
        <cms:param name="id">${data.id}</cms:param>
        <cms:param name="title">${data.title}</cms:param>
    </cms:include>
</c:forEach>