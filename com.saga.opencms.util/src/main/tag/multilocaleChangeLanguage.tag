<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.flex.CmsFlexController" %>
<%@ tag import="org.opencms.i18n.CmsMessages" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.opencms.util.CmsStringUtil" %>
<%@ tag import="java.util.ArrayList" %>
<%@ tag import="java.util.HashMap" %>
<%@ tag import="java.util.List" %>
<%@ tag import="java.util.Map" %>

<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Carga href y text para generar locales" %>

<%@attribute name="locales" type="java.lang.String" required="true" rtexprvalue="true"
             description="Locales separados por |" %>

<%@variable name-given="results" scope="NESTED" %>

<%!
    final Log LOG = CmsLog.getLog(this.getClass());
%>
<%
    List<Map> results = new ArrayList<Map>();
    try {
        String[] localesArray = locales.split("\\|");

        CmsObject cmso = CmsFlexController.getCmsObject(request);
        String uri = cmso.getRequestContext().getUri();
        String localeCurrent = cmso.getRequestContext().getLocale().getLanguage();

        String localePath = "/" + localeCurrent + "/";
        String relPath = uri.substring(uri.indexOf(localePath) + localePath.length() - 1);


        for (int i = 0; i < localesArray.length; i++) {
            String localeTarget = localesArray[i];
            if (!localeTarget.equals(localeCurrent)) {
                CmsMessages messages = new CmsMessages("com.saga.udl.formacioncontinua.messages", localeTarget);

                Map<String, Object> fields = new HashMap<String, Object>();

                // href
                String targetHref = CmsStringUtil.joinPaths("/", localeTarget, relPath);
                if (!cmso.existsResource(targetHref)) {
                    targetHref = CmsStringUtil.joinPaths("/", localeTarget, "/");
                }
                fields.put("href", targetHref);

                // text
                fields.put("text", messages.key("header.locale.link.text"));

                results.add(fields);
            }
        }
    } catch (Exception e) {
        LOG.error("e-cabecera-top", e);
    } finally {
        jspContext.setAttribute("results", results);
    }
%>
<jsp:doBody/>