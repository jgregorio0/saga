<%@ page import="com.saga.opencms.vue.SgSolrJson" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%
    Map<String, String> ctxt = new HashMap<String, String>();
    SgSolrJson solr = new SgSolrJson(request, ctxt);
%>
<div>HOLA</div>