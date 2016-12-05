<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="com.saga.opencms.util.SgSolr" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%
    // Parametros
    String jsonStr = request.getParameter("jsonStr");
    SgSolr.Json json = new SgSolr.Json(jsonStr);

    // Busqueda
    CmsObject cmso = CmsFlexController.getCmsObject(request);
    SgSolr solr = new SgSolr(cmso);
    String jContents = solr.search(json);
//    String templateResults = json.getTemplateResults(cmso, jContents);
%>
<%--<%=templateResults%>--%>
<cms:include file="<%=json.getTemplate()%>">
    <cms:param name="jsonStr"><%=jContents%></cms:param>
</cms:include>
