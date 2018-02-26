<%@ page import="org.opencms.ade.contenteditor.CmsContentService" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%-- The JSP HTML should be surrounded by block element --%>
<div>
    <%-- Read collector paramter, e.g. from request --%>
    <c:set var="folder" value="/tutorial/.content/texts/t_${number}.xml"/>
    <c:set var="type" value="bs-text"/>
    <c:set var="count" value="10"/>
    <ul>
        <%-- Use <cms:contentload> with new collector--%>
        <cms:contentload collector="allInSubTree" param="${folder}|${type}|${count}" editable="true">
            <%-- Access the content --%>
            <cms:contentaccess var="content"/>
            <c:set var="link"><cms:link>${content.filename}</cms:link></c:set>
            <li><a href="${link}">${content.value.Headline}</a></li>
            <li>uuid: ${content.id}</li>
            <li>rdfa.Headline: ${content.rdfa.Headline}</li>
            <li><%=CmsContentService.getEntityId()%></li>
        </cms:contentload>
    </ul>
</div>