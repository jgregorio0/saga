<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.apache.commons.io.IOUtils" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.http.Header" %>
<%@ page import="org.apache.http.HttpEntity" %>
<%@ page import="org.apache.http.auth.AuthenticationException" %>
<%@ page import="org.apache.http.client.methods.CloseableHttpResponse" %>
<%@ page import="org.apache.http.client.methods.HttpGet" %>
<%@ page import="org.apache.http.impl.client.CloseableHttpClient" %>
<%@ page import="org.apache.http.impl.client.HttpClients" %>
<%@ page import="org.opencms.json.JSONException" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="org.opencms.main.CmsLog" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>


<%!
    private Log LOG = CmsLog.getLog(this.getClass());

    String resStr;
    String url;

    int resCode;
    JSONObject json;

    HttpEntity entity;


    private void downloadZip(HttpServletResponse response, Long reportId)
            throws JSONException, IOException, AuthenticationException {
        url = "http" + "://" + "localhost" + ":" + 8080 + "/api" + "/reports/" + reportId + "/dicom/download";
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse res = client.execute(httpGet);
        resCode = res.getStatusLine().getStatusCode();
        entity = res.getEntity();

        Header[] headers = res.getAllHeaders();
        for (int i = 0; i < headers.length; i++) {
            response.setHeader(headers[i].getName(), headers[i].getValue());
        }

        InputStream in = entity.getContent();
        ServletOutputStream out = response.getOutputStream();
        IOUtils.copy(in, out);
        out.close();
        client.close();
    }
%>

<%
    Long estudioId = 1L;
    downloadZip(response, estudioId);
%>