<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONArray" %>
<%@ page import="org.opencms.json.JSONException" %>
<%@ page import="org.opencms.json.JSONObject" %>
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
    public String getJsonResults(CmsSolrResultList results, JSONArray fields) throws JSONException {
        List<Map<String, String>> contents = new ArrayList<Map<String, String>>();
        for (int iRes = 0; iRes < results.size(); iRes++) {
            Map<String, String> contenido = new HashMap<String, String>();
            CmsSearchResource result = results.get(iRes);
            for (int iField = 0; iField < fields.length(); iField++) {
                String field = fields.getString(iField);
                contenido.put(field, getSolrField(result, field));

            }
            contents.add(contenido);
        }
        return new JSONArray(contents).toString();
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
    String fieldsStr = request.getParameter("fields");

//    String query = request.getParameter("query");
//    String fields = request.getParameter("fields");

//    try {
//
//        // Obtenemos los datos del json
//        JSONObject json = new JSONObject(jsonStr);
////    HashMap<String,Object> json =
////            new ObjectMapper().readValue(jsonStr, HashMap.class);
//        String query = (String) json.get("query");
//        JSONArray fields = json.getJSONArray("fields");
////        ArrayList<String> fields = (ArrayList<String>) json.get("fields");
////    String template = (String) json.get("template");
//
//        // Busqueda
//        CmsObject cmso = CmsFlexController.getCmsObject(request);
//        String solrIndex = "Solr Offline";
//        if (cmso.getRequestContext().getCurrentProject().isOnlineProject()) {
//            solrIndex = "Solr Online";
//        }
//        CmsSolrResultList results = search(cmso, query, solrIndex);
//
//        jsonResults = getJsonResults(results, fields);
//    } catch (Exception e) {
//        LOG.error("ERROR searching and loading results for parametter: " + jsonStr, e);
//    }
//    finally {
//        request.setAttribute("jsonResults", jsonResults);
//    }
%>
<%--<%=jsonResults%>--%>
<%=queryStr%>||<%=fieldsStr%>
