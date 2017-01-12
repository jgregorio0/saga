<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.json.JSONArray" %>
<%@ page import="org.opencms.json.JSONException" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.search.CmsSearchException" %>
<%@ page import="org.opencms.search.CmsSearchResource" %>
<%@ page import="org.opencms.search.solr.CmsSolrQuery" %>
<%@ page import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ page import="org.opencms.staticexport.CmsLinkManager" %>
<%@ page import="org.opencms.util.CmsRequestUtil" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
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
     * @param baseUri
     * @param start
     * @return
     */
    public JSONArray getJsonResults(HttpServletRequest req, CmsSolrResultList results, String[] fields, String baseUri, Long start) throws JSONException {
        List<Map<String, String>> contents = new ArrayList<Map<String, String>>();
        for (int iRes = 0; iRes < results.size(); iRes++) {
            Map<String, String> contenido = new HashMap<String, String>();
            CmsSearchResource result = results.get(iRes);
            for (int iField = 0; iField < fields.length; iField++) {
                String field = fields[iField];
                contenido.put(field, getSolrField(result, field));
            }

            // Add detail page
            if (baseUri != null) {
                String link = cmsLink(req, result.getRootPath(), baseUri);
                contenido.put("link", link);
            }

            // Add resource idx
            if (start != null) {
                try {
                    long idx = start + iRes;
                    String idxStr = String.valueOf(idx);
                    contenido.put("idx", idxStr);
                } catch (Exception e) {
                    LOG.debug("no ha sido posible calcular el indice para start: " + start + " iRes: " + iRes);
                }
            }
            contents.add(contenido);
        }
        return new JSONArray(contents);
    }

    /**
     *
     * @param results
     * @param fields
     * @return
     */
    public List<Map<String, String>> getMapResults(CmsSolrResultList results, String[] fields) throws JSONException {
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
        return contents;
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

    private String cmsLink(HttpServletRequest req, String target, String baseUri){
        CmsFlexController controller = CmsFlexController.getController(req);
        // be sure the link is absolute
        String uri = CmsLinkManager.getAbsoluteUri(target, controller.getCurrentRequest().getElementUri());
        CmsObject cms = controller.getCmsObject();
        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(baseUri)) {
            try {
                cms = OpenCms.initCmsObject(cms);
                cms.getRequestContext().setUri(baseUri);
            } catch (CmsException e) {
                // should not happen, if it does we can't do anything useful and will just keep the original object
            }
        }
        // generate the link
        return OpenCms.getLinkManager().substituteLinkForUnknownTarget(cms, uri);
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
    String uri = request.getParameter("uri");
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

        Long idx = Long.valueOf(idxStr);

        // Solo devolvemos resultados si el indice es menor que el maximo
        if (idx > total) {
            jsonResults = "{" +
                    "\"st\":\"ok\"" +
                    ", \"total\": " + total +
                    ", \"numResults\": " + 0 +
                    ", \"results\": []" +
                    ", \"idx\": " + idxStr + "}";
        } else {
            // Obtenemos los campos que necesitamos
            JSONArray jArrayResults = getJsonResults(request, results, fields, uri, idx);
            int numResults = jArrayResults.length();
            String resultsArray = jArrayResults.toString();
            jsonResults = "{" +
                    "\"st\":\"ok\"" +
                    ", \"total\": " + total +
                    ", \"numResults\": " + numResults +
                    ", \"results\": " + resultsArray +
                    ", \"idx\": " + idxStr + "}";
        }
    } catch (Exception e) {
        LOG.error("searching or loading results for query: " + queryStr + " and fields: " + fieldsStr, e);
        jsonResults = "{\"st\":\"error\"}";
    }
//    finally {
//        request.setAttribute("jsonResults", jsonResults);
//    }
%>
<%=jsonResults%>