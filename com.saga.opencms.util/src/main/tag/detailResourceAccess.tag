<%@ tag import="org.opencms.jsp.util.CmsJspContentAccessBean" %>
<%@ tag import="org.opencms.jsp.util.CmsJspContentAccessValueWrapper" %>
<%@ tag import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ tag import="java.util.Map" %>

<%@ tag trimDirectiveWhitespaces="true" pageEncoding="UTF-8"
        description="Permite acceder al contenido del recurso que se muestra en detalle" %>

<%
    CmsJspContentAccessBean content = null;
    Map<String, CmsJspContentAccessValueWrapper> value = null;
    Map<String, String> rdfa = null;
    CmsJspStandardContextBean cms = CmsJspStandardContextBean.getInstance(request);
    if (cms.isDetailRequest()) {
        content = new CmsJspContentAccessBean(cms.getVfs().getCmsObject(), cms.getDetailContent());
        value = content.getValue();
        rdfa = content.getRdfa();
    }
    request.setAttribute("content", content);
    request.setAttribute("value", value);
    request.setAttribute("rdfa", rdfa);
%>