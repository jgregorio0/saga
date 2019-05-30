<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.file.CmsResource" %>
<%@ tag import="org.opencms.jsp.util.CmsJspContentAccessBean" %>
<%@ tag import="org.opencms.jsp.util.CmsJspContentAccessValueWrapper" %>
<%@ tag import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ tag import="java.util.Map" %>

<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Permite acceder al contenido del recurso que se muestra en detalle" %>

<%@attribute name="path" type="java.lang.String" required="true" rtexprvalue="true"
             description="Ruta absoluta al recurso" %>

<%@attribute name="contentVarName" type="java.lang.String" required="false" rtexprvalue="true"
             description="Nombre de la variable en la que guardará el CmsJspContentAccessBean en el request " %>
<%@attribute name="valueVarName" type="java.lang.String" required="false" rtexprvalue="true"
             description="Nombre de la variable en la que guardará el Map<String, CmsJspContentAccessValueWrapper> en el request " %>
<%@attribute name="rdfaVarName" type="java.lang.String" required="false" rtexprvalue="true"
             description="Nombre de la variable en la que guardará el Map<String, String> en el request " %>

<%
    CmsJspContentAccessBean content = null;
    Map<String, CmsJspContentAccessValueWrapper> value = null;
    Map<String, String> rdfa = null;
    CmsJspStandardContextBean cms = CmsJspStandardContextBean.getInstance(request);
    CmsObject cmso = cms.getVfs().getCmsObject();

    String resourcePath = path;
    String siteRoot = cmso.getRequestContext().getSiteRoot();
    if (resourcePath.contains(siteRoot)) {
        resourcePath = resourcePath.substring(siteRoot.length());
    }
    CmsResource resource = cmso.readResource(resourcePath);

    content = new CmsJspContentAccessBean(cmso, resource);
    value = content.getValue();
    rdfa = content.getRdfa();

    if(contentVarName != null) {
        request.setAttribute(contentVarName, content);
    }
    else{
        request.setAttribute("content", content);
    }
    if(valueVarName != null) {
        request.setAttribute(valueVarName, value);
    }
    else{
        request.setAttribute("value", value);
    }
    if(rdfaVarName != null) {
        request.setAttribute(rdfaVarName, rdfa);
    }
    else{
        request.setAttribute("rdfa", rdfa);
    }
%>