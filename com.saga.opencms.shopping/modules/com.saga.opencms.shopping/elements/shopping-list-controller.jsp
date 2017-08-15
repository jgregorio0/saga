<%@ page import="com.jgregorio.opencms.fileupload.SgSolrJson" %>
<%@ page import="com.google.gwt.dev.util.collect.HashMap" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.opencms.json.JSONObject" %>
<%@ page import="com.jgregorio.opencms.fileupload.SgJson" %>
<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%
    JSONObject json = new JSONObject();

    try {
        CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
        HashMap<String, String> ctxt = new HashMap<String, String>();
        ctxt.put(SgSolrJson.LOCALE, cmso.getRequestContext().getLocale().getLanguage());
        ctxt.put(SgSolrJson.SITE, cmso.getRequestContext().getSiteRoot());
        ctxt.put(SgSolrJson.URI, cmso.getRequestContext().getUri());

        SgSolrJson solr = new SgSolrJson(request, ctxt);

        String fqType = "fq=type:\"function\"";

        String idsParam = request.getParameter("ids");
        String fqIds = "";
        if (StringUtils.isNotBlank(idsParam)) {
            String[] ids = idsParam.split("\\|");
            String idsStr = "";
            for (int i = 0; i < ids.length; i++) {
                String id = ids[i];
                if (idsStr.length() > 0) {
                    idsStr = idsStr + " OR ";
                }
                idsStr = idsStr + "\"" + id + "\"";
            }
            fqIds = "fq=id:(" + idsStr + ")";
        }

        String query = fqType + fqIds;

        String fields = "id,path,Title_prop";

        json = solr.searchSolrFields(query, fields);
    } catch (Exception e) {
        json = SgJson.errorJResponse(e);
    } finally {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
%>