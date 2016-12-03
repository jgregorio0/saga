<%@ page import="com.fasterxml.jackson.databind.ObjectMapper" %>
<%@ page import="java.util.ArrayList" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%
    String jsonStr = request.getParameter("jsonStr");
    pageContext.setAttribute("jContents",
            new ObjectMapper().readValue(jsonStr, ArrayList.class));
%>
<div id="contenidos">
    <c:forEach var="jContent" items="${jContents}" varStatus="status">
        <div id="contenido-${status.index}">
            <div class="col-md-6" style="text-align:right">
                <h1>${jContent.title}</h1>
                <p>${jContent.filename}</p>
                <p>${jContent.path}</p>
            </div>
        </div>
    </c:forEach>
</div>