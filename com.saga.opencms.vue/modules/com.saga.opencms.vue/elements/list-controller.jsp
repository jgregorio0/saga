<%@ page import="com.saga.opencms.vue.SgSolrJson" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%--LOAD DATA FROM PARAMETERS--%>
<%!
    private String locale;
    private String site;
    private String uri;
    private String index;

    private String query;
    private String fields;

    private void load(HttpServletRequest request) {
        String p1 = request.getParameter("param1");
        String p2 = request.getParameter("param2");
        query = p1 + p2;

        fields = "id,path,link";

        locale = request.getParameter("locale");
        site = request.getParameter("site");
        uri = request.getParameter("uri");
        index = request.getParameter("index");
    }

    private Map<String, String> loadCtxt() {
        Map<String, String> ctxt = new HashMap<String, String>();
        ctxt.put("locale", locale);
        ctxt.put("site", site);
        ctxt.put("uri", uri);
        ctxt.put("index", index);

        return ctxt;
    }

    private boolean validate() throws Exception {
        if (locale == null) {
            throw new Exception("locale must not be empty");
        }

        return true;
    }
%>
<%
    JSONObject jRes = new JSONObject();
    try {
        load(request);
        if (validate()) {
            Map<String, String> ctxt = loadCtxt();
            SgSolrJson solr = new SgSolrJson(request, ctxt);
            jRes = solr.searchSolrFields(query, fields);
        }
    } catch (Exception e) {
        jRes = SgSolrJson.errorJResponse(e);
    } finally {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jRes.toString());
    }
%>