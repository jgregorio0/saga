<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Gener el HTML con el listado de cursos del profesor" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ tag import="com.saga.udl.formacioncontinua.util.SgSolr" %>
<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.apache.solr.client.solrj.response.FacetField" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.json.JSONArray" %>
<%@ tag import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ tag import="java.util.List" %>

<%@attribute name="query" type="java.lang.String" required="true" rtexprvalue="true"
             description='Solr query &fq=parent-folders:"/sites/centroformacion/ca/"&fq=type:udlcf-curso&fq=con_locales:ca' %>
<%@attribute name="facet" type="java.lang.Boolean" required="false" rtexprvalue="true"
             description="Using facet" %>

<%@variable name-given="results" scope="NESTED" %>
<%@variable name-given="facets" scope="NESTED" %>
<%@variable name-given="error" scope="NESTED" %>

<%--
 SOLR
 -------

 query (mandatory): Solr query &fq=parent-folders:"/sites/centroformacion/ca/"&fq=type:udlcf-curso&fq=con_locales:ca

 --%>
<%!
    final Log LOG = CmsLog.getLog("com.saga.udl.formacioncontinua.tags.udl.solrSearch");
%>
<%
    boolean error = false;
    CmsSolrResultList results = null;
    JSONArray jResults = null;
    List<FacetField> facets = null;
    JSONArray jFacets = null;
    try {
        LOG.debug("query: " + query);
        CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
        results = new SgSolr(cmso).search(query);
        if (facet != null && facet) {
            facets = results.getFacetFields();
        }
        LOG.debug("results: " + results.getNumFound());
    } catch (Exception e) {
        LOG.error("searcing solr", e);
        error = true;
    } finally {
        jspContext.setAttribute("results", results);
        jspContext.setAttribute("facets", facets);
        jspContext.setAttribute("error", error);
    }
%>
<jsp:doBody/>