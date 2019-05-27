<%@ tag import="org.apache.commons.lang3.StringUtils" %>
<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.jsp.CmsJspTagLink" %>
<%@ tag import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
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

    private String getTargetForDetailPage(
            HttpServletRequest request, CmsJspStandardContextBean controller, CmsObject cmso, String targetHref) {
        String sitePath = cmso.getSitePath(controller.getDetailContent());
        targetHref = CmsJspTagLink.linkTagAction(sitePath, request, targetHref);
        LOG.debug("pagina de detalle del recurso " + sitePath + " en " + targetHref);
        return targetHref;
    }
%>
<%
    List<Map> results = new ArrayList<Map>();
    try {
        String[] localesArray = locales.split("\\|");

        CmsJspStandardContextBean controller = CmsJspStandardContextBean.getInstance(request);
        CmsObject cmso = controller.getVfs().getCmsObject();
        String uri = cmso.getRequestContext().getUri();
        String queryString = request.getQueryString();
        String localeCurrent = cmso.getRequestContext().getLocale().getLanguage();


        String localePath = "/" + localeCurrent + "/";
        String relPath = uri.substring(uri.indexOf(localePath) + localePath.length() - 1);


        for (int i = 0; i < localesArray.length; i++) {
            String localeTarget = localesArray[i];
            if (!localeTarget.equals(localeCurrent)) {
                Map<String, Object> fields = new HashMap<String, Object>();
                // obtenemos enlace sustituyendo locale
                String targetHref = CmsStringUtil.joinPaths("/", localeTarget, relPath);
                LOG.debug("obtenemos enlace sustituyendo locale " + targetHref);
                if (controller.isDetailRequest()) {
                    // 1- Obtenemos enlace a pagina de detalle con target al sitePath del recurso y baseUri como uri en idioma destino
                    targetHref = getTargetForDetailPage(request, controller, cmso, targetHref);
                } else if (!cmso.existsResource(targetHref)) {
                    // 2- no es pagina de detalle y no existe enlace sustituyendo locale, enviamos a la home
                    targetHref = CmsStringUtil.joinPaths("/", localeTarget, "/");
                    LOG.debug("no es pagina de detalle y no existe enlace sustituyendo locale, enviamos a la home " + targetHref);
                }
                // mantenemos parametros de la URL
                if (StringUtils.isNotBlank(queryString) && StringUtils.isNotBlank(targetHref)) {
                    targetHref += "?" + queryString;
                }
                // href and locale result
                fields.put("href", targetHref);
                fields.put("locale", localeTarget);
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