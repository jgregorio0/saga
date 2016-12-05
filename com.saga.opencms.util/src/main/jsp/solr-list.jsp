<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%
//    fq=parent-folders:'/sites/default/'&fq=type:('function')&sort=created desc&rows=10&start=0
    String query = "fq=parent-folders:\\\"/sites/default/\\\"" +
            "&fq=type:(\\\"function\\\")" +
            "&sort=created desc" +
            "&rows=10" +
            "&start=0";
    String fields = "[\"Title_prop_s\", \"filename\", \"path\"]";
    String template = "/system/modules/com.saga.opencms.solrlist/elements/solr-list-template.jsp";
    String jsonStr = "{" +
            "\"query\": \"" + query + "\"" +
            ", \"fields\": " + fields +
            ", \"template\": \"" + template + "\"" +
            "}";
%>

<div>
    <%--<p>jQuery <%=jQuery%></p>--%>
    <%--<p>jFields <%=jFields%></p>--%>
    <%--<p>jTemplate <%=jTemplate%></p>--%>
    <cms:include file="%(link.strong:/system/modules/com.saga.opencms.solrlist/elements/solr-list-controller.jsp)">
        <cms:param name="jsonStr"><%=jsonStr%></cms:param>
    </cms:include>
</div>