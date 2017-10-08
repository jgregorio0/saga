<%@ page import="com.saga.opencms.vue.SgSolrJson" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%--LOAD DATA FROM PARAMETERS--%>
<%!
    final Log LOG = CmsLog.getLog(this.getClass());

    private String locale;
    private String site;
    private String uri;
    private String index;

    private String query;
    private String fields;

    private void load(HttpServletRequest request) {
        // Rows
        int iRows = 10;
        String rows = request.getParameter("rows");
        if (StringUtils.isNotBlank(rows)) {
            try {
                iRows = Integer.valueOf(rows);
            } catch (Exception e) {
                LOG.error("rows parameter must be int " + rows, e);
            }
        }
        String qRows = "&rows=" + iRows;

        // Start
        int iStart = 0;
        String start = request.getParameter("start");
        if (StringUtils.isNotBlank(start)) {
            try {
                iStart = Integer.valueOf(start);
            } catch (Exception e) {
                LOG.error("start parameter must be int " + start, e);
            }
        }
        String qStart = "&start=" + iStart;

        query = "fq=parent-folders:\"/sites/default/\"" +
                "&fq=type:image" +
                qRows +
                qStart;

        fields = "id,path,Title_prop";

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