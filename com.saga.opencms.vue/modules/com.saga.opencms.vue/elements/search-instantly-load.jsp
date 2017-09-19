<%@ page import="org.json.JSONArray" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="com.saga.opencms.vue.CmsSolrUtil" %>
<%@ page import="org.opencms.search.solr.CmsSolrResultList" %>
<%@ page import="org.opencms.search.CmsSearchResource" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="org.opencms.jsp.CmsJspTagLink" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%!
    HttpServletRequest request;

    /**
     * From each solr result take Solr field value.
     * For example, from
     * {"url" : "rootPath", "title" : "Title_prop"}
     * returns
     * [{"url" : "/root/path1", "title" : "Title1"}, {"url" : "/root/path2", "title" : "Title2"}]
     *
     * @param results
     * @param jsonFields
     * @return
     */
    public JSONArray toJSONArray(CmsSolrResultList results, JSONObject jsonFields) {
        JSONArray jsonArray = new JSONArray();

        // For each result
        for (int i = 0; i < results.size(); i++) {
            CmsSearchResource res = results.get(i);

            // Found the fields
            JSONObject jsonSolr = new JSONObject();
            Iterator<String> fields = jsonFields.keys();
            while (fields.hasNext()) {
                String fieldName = fields.next();
                String solrName = jsonFields.getString(fieldName);
                String solrValue = getFieldValue(res, solrName);
                if (fieldName != null && solrValue != null) {
                    jsonSolr.put(fieldName, solrValue);
                }
            }
            if (jsonSolr.length() > 0) {
                jsonArray.put(jsonSolr);
            }
        }
        return jsonArray;
    }

    /**
     * Return the field value from solr search resource
     * @param res
     * @param fieldName
     * @return
     */
    private String getFieldValue(CmsSearchResource res, String fieldName) {
        String fieldValue;
        if (fieldName.equals("rootPath")) {
            String rootPath = res.getRootPath();
            fieldValue = CmsJspTagLink.linkTagAction(rootPath, request);
        } else if (fieldName.equals("name")) {
            fieldValue = res.getName();
        } else {
            fieldValue = res.getField(fieldName);
        }
        return fieldValue != null ? fieldValue : "";
    }
%>

<%
    this.request = request;

    // Obtenemos los filtros de busqueda
    String jsonStr = request.getParameter("json");
    JSONObject jsonParam = new JSONObject(jsonStr);

    // Obtenemos la query
    String query = jsonParam.getString("query");
    JSONObject jsonFields = jsonParam.getJSONObject("fields");

    // Hacemos la busqueda en solr
    CmsObject cmso = CmsFlexController.getCmsObject(request);
    CmsSolrUtil solrUtil = new CmsSolrUtil(cmso);
    CmsSolrResultList results = solrUtil.search(query);

    // Pasamos los resultados a JSONArray
    JSONArray jsonArray = toJSONArray(results, jsonFields);

    // Devolvemos la respuesta
    response.getWriter().print(jsonArray);
%>