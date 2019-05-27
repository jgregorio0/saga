<%@ tag import="org.apache.commons.lang3.StringUtils" %>
<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.jsp.CmsJspResourceWrapper" %>
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
    private String getTargetHome(String localeTarget) {
        String targetHref;
        targetHref = CmsStringUtil.joinPaths("/", localeTarget, "/");
        LOG.debug("no existe la pagina con sistema multidioma de la 10.5 redirigimos a la home " + targetHref);
        return targetHref;
    }

    private String getTargetMultiLanguage(CmsJspStandardContextBean controller, String localeTarget, String targetHref) {
        LOG.debug("obtenemos enlace con sistema multidioma de la 10.5 con localeTarget " + localeTarget);
        CmsJspResourceWrapper localeResource = controller.getLocaleResource().get(localeTarget);
        if (localeResource != null) {
            targetHref = localeResource.getLink();
        }
        LOG.debug("obtenemos enlace con sistema multidioma de la 10.5 " + targetHref);
        return targetHref;
    }

    private String getTargetForDetailPage(
            HttpServletRequest request, CmsJspStandardContextBean controller, CmsObject cmso, String targetHref) {
        String sitePath = cmso.getSitePath(controller.getDetailContent());
        targetHref = CmsJspTagLink.linkTagAction(sitePath, request, targetHref);
        LOG.debug("pagina de detalle del recurso " + sitePath + " en " + targetHref);
        return targetHref;
    }

    final Log LOG = CmsLog.getLog(this.getClass());
%>
<%
    List<Map> results = new ArrayList<Map>();
    try {
        CmsJspStandardContextBean controller = CmsJspStandardContextBean.getInstance(request);
        CmsObject cmso = controller.getVfs().getCmsObject();
        String uri = cmso.getRequestContext().getUri();
        String queryString = request.getQueryString();

        // habilita multilocale
        boolean multilocaleEnabled = false;
        try {
            String propValue = cmso.readPropertyObject(uri, "sagasuite.multilocale", true).getValue();
            multilocaleEnabled = propValue != null && propValue.equals("true");
        } catch (Exception e) {
        }
        if (multilocaleEnabled) {
            // locales, uri y ruta relativa al idioma
            String[] localesArray = locales.split("\\|");
            String localeCurrent = cmso.getRequestContext().getLocale().getLanguage();
            String localePath = "/" + localeCurrent + "/";
            String relPath = uri.substring(uri.indexOf(localePath) + localePath.length() - 1);
            // por cada locale obtenemos enlace a la ruta actual en el idioma de destino
            for (int i = 0; i < localesArray.length; i++) {
                // para los locales configurados
                String localeTarget = localesArray[i];
                if (!localeTarget.equals(localeCurrent)) {
                    // solo para los locales que no sean el actual
                    Map<String, Object> fields = new HashMap<String, Object>();
                    // obtenemos enlace sustituyendo locale
                    String targetHref = CmsStringUtil.joinPaths("/", localeTarget, relPath);
                    LOG.debug("obtenemos enlace sustituyendo locale " + targetHref);
                    if (controller.isDetailRequest()) {
                        // 1- Obtenemos enlace a pagina de detalle con target al sitePath del recurso y baseUri como uri en idioma destino
                        targetHref = getTargetForDetailPage(request, controller, cmso, targetHref);
                    }
                    // 2- no es pagina de detalle, obtenemos enlace sustituyendo locale
                    else if (!cmso.existsResource(targetHref)) {
                        // 3- no es pagina de detalle y no existe enlace sustituyendo locale obtenemos enlace con sistema multidioma 10.5
                        targetHref = getTargetMultiLanguage(controller, localeTarget, targetHref);
                        if (!cmso.existsResource(targetHref)) {
                            // 4- no es pagina de detalle y no existe la pagina redirigimos a la home
                            targetHref = getTargetHome(localeTarget);
                        }
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
        }

    } catch (Exception e) {
        LOG.error("e-cabecera-top", e);
    } finally {
        jspContext.setAttribute("results", results);
    }
%>
<jsp:doBody/>