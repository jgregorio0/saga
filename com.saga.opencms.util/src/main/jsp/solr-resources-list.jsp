<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.search.CmsSearchException" %>
<%@ page import="org.opencms.search.CmsSearchResource" %>
<%@ page import="org.opencms.search.solr.CmsSolrQuery" %>
<%@ page import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ page import="org.opencms.util.CmsRequestUtil" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%
/**
 * JSP que realiza una búsqueda por solr y devuelve un listado con los resultados
 *
 * Por defecto la query contiene:
 * fq=expired:[NOW TO *]
 *        &con_locales:es
 *        &parent-folders:"/sites/chefcaprabo/"
 *        &released:[* TO NOW]
 *          q=*:*
 *          fl=*,score
 *          qt=edismax
 *          rows=10
 *          start=0
 *
 * Recibe los parametros
 * query: query de búsqueda
 *

*/
%>

<%!
    private String getSolrField(CmsSearchResource result, String field) {
        String res = result.getField(field);
        res = res == null ? "" : res;
        return res;
    }

    public CmsSolrResultList search(CmsObject cmso, String query) throws CmsSearchException {
        String solrIndex = "Solr Offline";
        if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
            solrIndex = "Solr Online";
        }

        Map<String, String[]> parameters = CmsRequestUtil.createParameterMap(query);
        CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, parameters);
        return OpenCms.getSearchManager().getIndexSolr(
                solrIndex).search(cmso, solrQuery, true);
    }
%>
<%
    //default
    // fq=expired:[NOW TO *]
    //      &con_locales:es
    //      &parent-folders:"/sites/chefcaprabo/"
    //      &released:[* TO NOW]
    // q=*:*
    // fl=*,score
    // qt=edismax
    // rows=10
    // start=0
//    StringBuffer query = new StringBuffer();
//
//    // parent folder shared
//    query.append("fq=parent-folders:\"/shared/\"");
//
//    // type MixRecipeChef OR RecipeChef
//    query.append("&fq=type:(\"MixRecipeChef\" OR \"RecipeChef\")");
//
//    // sort created
//    query.append("&sort=created desc");
//
//    // rows 3
//    query.append("&rows=3");
//
//    // start 0
//    query.append("&start=0");

    // Parametros
//    String query = request.getParameter("query");
//    int idx = 0;
//    try {
//        idx = Integer.valueOf(request.getParameter("idx"));
//    } catch (Exception e) {}
    // Parametros
    String query = request.getParameter("query");

    String titleField = request.getParameter("xmltitle");
    String imageField = request.getParameter("xmlimage");
    String descField = request.getParameter("xmldescription");

    // Busqueda de contenidos
    CmsObject cmso = CmsFlexController.getCmsObject(request);
    String lang = cmso.getRequestContext().getLocale().getLanguage();
    CmsSolrResultList results = search(cmso, query.toString());
    List<Map<String, String>> contenidos = new ArrayList<Map<String, String>>();

    for (int i = 0; i < results.size(); i++) {
        Map<String, String> contenido = new HashMap<String, String>();
        CmsSearchResource result = results.get(i);

        String path = result.getRootPath();
        contenido.put("path", path);

        String image = getSolrField(result, imageField);
        contenido.put("image", image);

        String title = getSolrField(result, titleField);
        contenido.put("title", title);

        String description = getSolrField(result, descField);
        contenido.put("description", description);

        contenidos.add(contenido);
    }
    pageContext.setAttribute("contenidos", contenidos);
%>
    <%--DESKTOP--%>
    <c:forEach var="contenido" items="${contenidos}">
        <div id="idNotRel">

            <%--Display desktop??--%>
             <%--style="display:none;">--%>
            <div class="col-xs-12 col-sm-4 col-md-12 rel-vertical">
                <div class="hidden-sm col-md-6" style="text-align:right">
                    <h6 class="WS-Regular" id="idNotRel_Tit">
                        <b>${fn:toUpperCase(contenido.get("title"))}</b>
                        <br><br>
                        <c:set var="desc">${contenido.get("description")}</c:set>
                        <c:if test='${not empty desc && desc.length() > 100}'>
                            <c:set var="desc">${fn:substring(desc, 0, 96)} ...</c:set>
                        </c:if>
                        ${desc}
                    </h6>
                </div>
                <div class="col-xs-6 col-sm-12 col-md-6">
                    <a id="idNotRel_Link" href="<cms:link>${contenido.get("path")}</cms:link>">
                        <img id="idNotRel_Img" src="<cms:link>${contenido.get("image")}</cms:link>" class="img-responsive">
                    </a>
                </div>
                <div class="col-sm-12 visible-sm" style="text-align:center">
                    <h6 class="WS-Regular" id="idNotRel_TitBis"></h6>
                </div>
            </div>
        </div>
    </c:forEach>