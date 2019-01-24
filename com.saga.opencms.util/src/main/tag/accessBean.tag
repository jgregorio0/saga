<%@ tag import="org.opencms.file.CmsObject" %>
<%@ tag import="org.opencms.file.CmsResource" %>
<%@ tag import="org.opencms.jsp.util.CmsJspContentAccessBean" %>
<%@ tag import="org.opencms.jsp.util.CmsJspContentAccessValueWrapper" %>
<%@ tag import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ tag import="java.util.Map" %>

<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Permite acceder al contenido del recurso que se muestra en detalle" %>
<%@attribute name="sitePath" type="java.lang.String" required="true" rtexprvalue="true"
             description="Ruta absoluta al recurso" %>

<%
    CmsJspContentAccessBean content = null;
    Map<String, CmsJspContentAccessValueWrapper> value = null;
    Map<String, String> rdfa = null;
    CmsJspStandardContextBean cms = CmsJspStandardContextBean.getInstance(request);
    CmsObject cmso = cms.getVfs().getCmsObject();
    CmsResource resource = cmso.readResource(sitePath);
    content = new CmsJspContentAccessBean(cmso, resource);
    value = content.getValue();
    rdfa = content.getRdfa();
    request.setAttribute("content", content);
    request.setAttribute("value", value);
    request.setAttribute("rdfa", rdfa);
%>