<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONArray" %>
<%@ page import="org.opencms.json.JSONException" %>
<%@ page import="org.opencms.main.CmsLog" %>
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
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@page buffer="none" session="true" trimDirectiveWhitespaces="true"%>

<%
    /**
     * Carga resultados por solr segun los parametros dados
     *
     * query: Query de Solr
     * fields: Campos que se desean
     */
%>

<%!
    final Log LOG = CmsLog.getLog(this.getClass());

    /**
     * Devuelve la lista de resultados encontrados por la busqueda Solr
     * dada la query como parametro.
     * Por defecto OpenCms añade los parametros:
     * default
     * fq=expired:[NOW TO *]
     *            &con_locales:es
     *            &parent-folders:"/sites/chefcaprabo/"
     *            &released:[* TO NOW]
     * q=*:*
     * fl=*,score
     * qt=edismax
     * rows=10
     * start=0
     *
     * @param query
     */
    public CmsSolrResultList search(CmsObject cmso, String query, String solrIndex) throws CmsSearchException {
        Map<String, String[]> parameters = CmsRequestUtil.createParameterMap(query);
        CmsSolrQuery solrQuery = new CmsSolrQuery(cmso, parameters);
        return OpenCms.getSearchManager().getIndexSolr(
                solrIndex).search(cmso, solrQuery, true);
    }

    /**
     *
     * @param results
     * @param fields
     * @return
     */
    public JSONArray getJsonResults(CmsSolrResultList results, String[] fields) throws JSONException {
        List<Map<String, String>> contents = new ArrayList<Map<String, String>>();
        for (int iRes = 0; iRes < results.size(); iRes++) {
            Map<String, String> contenido = new HashMap<String, String>();
            CmsSearchResource result = results.get(iRes);
            for (int iField = 0; iField < fields.length; iField++) {
                String field = fields[iField];
                contenido.put(field, getSolrField(result, field));
            }
            contents.add(contenido);
        }
        return new JSONArray(contents);
    }

    /**
     * Find solr field. If it does not exist return empty String.
     * @param result
     * @param field
     * @return
     */
    public static String getSolrField(CmsSearchResource result, String field) {
        String res = result.getField(field);
        res = res == null ? "" : res;
        return res;
    }
%>

<%
    String jsonResults = "{}";

    // Parametros
    String queryStr = request.getParameter("query");
    String query = queryStr.replace("'", "\"");
    String fieldsStr = request.getParameter("fields");
    String[] fields = fieldsStr.split(",");
    String idxStr = request.getParameter("idx");
    LOG.debug("buscador tags query: " + query + " fields: " + fieldsStr);

    try {

        // Busqueda
        CmsObject cmso = CmsFlexController.getCmsObject(request);
        String solrIndex = "Solr Offline";
        if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
            solrIndex = "Solr Online";
        }
        CmsSolrResultList results = search(cmso, query, solrIndex);
        long total = results.getNumFound();
        LOG.debug("buscador tags resultados: " + results.size());

        // Obtenemos los campos que necesitamos

        JSONArray jArrayResults = getJsonResults(results, fields);
//        int total = jArrayResults.length();
        String resultsArray = jArrayResults.toString();
        jsonResults = "{" +
                "\"st\":\"ok\"" +
                ", \"total\": " + total +
                ", \"results\": " + resultsArray +
                ", \"idx\": " + idxStr + "}";
    } catch (Exception e) {
        LOG.error("searching or loading results for query: " + queryStr + " and fields: " + fieldsStr, e);
        jsonResults = "{\"st\":\"error\"}";
    }
//    finally {
//        request.setAttribute("jsonResults", jsonResults);
//    }
%>
<%=jsonResults%>
<%--<%=solrStr%>--%>
<%--<%=queryStr%>||<%=fieldsStr%>--%>