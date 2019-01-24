<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="udl" tagdir="/WEB-INF/tags/udl" %>
<%--<%
    String sitePath = "/.content/.template-elements/cabecera-theme";
    CmsJspContentAccessBean content = null;
    Map<String, CmsJspContentAccessValueWrapper> value = null;
    Map<String, String> rdfa = null;
    CmsJspStandardContextBean cms = CmsJspStandardContextBean.getInstance(request);
//    if (cms.isDetailRequest()) {
        CmsObject cmso = cms.getVfs().getCmsObject();
        CmsResource resource = cmso.readResource(sitePath);
        content = new CmsJspContentAccessBean(cmso, resource);
        value = content.getValue();
        rdfa = content.getRdfa();
//    }
    request.setAttribute("content", content);
    request.setAttribute("value", value);
    request.setAttribute("rdfa", rdfa);
%>--%>
<div>
    <udl:accessBean sitePath="%(link.weak:/.content/.template-elements/cabecera-theme.xml)"/>
    <p>${content.value.Title}</p>
    ${value.TopHeader}
</div>