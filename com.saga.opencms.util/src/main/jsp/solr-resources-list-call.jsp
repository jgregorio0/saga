<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%
    private class Query {

    }
    String jsonStr = "{" +
            "\"query\": " +
            "}";
%>

<div>
    <cms:include file="%(link.strong:/sites/default/solr-resources-list-call.jsp)">
        <cms:param name="json">{}</cms:param>
    </cms:include>
</div>