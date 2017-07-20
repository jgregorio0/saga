<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Lee un campo del contenido xml" %>

<%@ tag import="org.apache.commons.logging.Log" %>
<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.flex.CmsFlexController" %>
<%@ tag import="org.opencms.main.CmsLog" %>
<%@ tag import="org.opencms.xml.content.CmsXmlContent" %>
<%@ tag import="java.util.Locale" %>

<%@ attribute name="var" type="java.lang.String" required="true" rtexprvalue="true"
             description="Nombre de la variable con el valor del campo" %>
<%@ attribute name="xmlPath" type="java.lang.String" required="true" rtexprvalue="true"
             description="Ruta del campo contenido en el contenido xml" %>
<%@ attribute name="lang" type="java.lang.String" required="false" rtexprvalue="true"
              description="Locale del que se van a leer el valor de los campos" %>

<%--
- var (Obligatorio): 'title'
- xmlPath (Obligatorio): 'Title' o 'Content[2]/Title'
- lang (Opcional): 'es'
--%>

<%!
    Log LOG = CmsLog.getLog("_TAG_RESOURCE_XML_FIELD");

    private Locale initLocale(CmsObject cmso, String locale) {
        Locale loc;
        if (locale == null){
            loc = cmso.getRequestContext().getLocale();
        } else {
            loc = new Locale(locale);
        }
        return loc;
    }
%>
<%
    String fieldValue = null;
    try {
        CmsObject cmso = CmsFlexController.getCmsObject(request);
        CmsXmlContent xmlResource = (CmsXmlContent)request.getAttribute("_xmlResource");

        Locale locale = initLocale(cmso, lang);
        fieldValue = xmlResource.getStringValue(cmso, xmlPath, locale);
    } catch (Exception e) {
        LOG.error(e);
    } finally {
        request.setAttribute(var, fieldValue);
    }
%>