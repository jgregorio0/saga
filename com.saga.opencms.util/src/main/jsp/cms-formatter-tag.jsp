<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.jsp.util.CmsJspContentAccessBean" %>
<%@ page import="org.opencms.xml.I_CmsXmlDocument" %>
<%@ page import="org.opencms.xml.content.CmsXmlContentFactory" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>


<%// Cargamos el evento del parametro
    try {
        String resPath = (String)request.getAttribute("e");
        if (resPath != null) {
            CmsObject cmso = CmsFlexController.getCmsObject(request);
            CmsResource resource = cmso.readResource(resPath);
            CmsFile eventFile = cmso.readFile(resource);

            // Cargamos el contenido
            CmsJspContentAccessBean contentBean;
            I_CmsXmlDocument xmlContent = CmsXmlContentFactory.unmarshal(cmso, eventFile);
            contentBean = new CmsJspContentAccessBean(cmso, cmso.getRequestContext().getLocale(), xmlContent);

            pageContext.setAttribute("content", contentBean, PageContext.PAGE_SCOPE);
            pageContext.setAttribute("value", contentBean.getValue(), PageContext.PAGE_SCOPE);
            pageContext.setAttribute("rdfa", contentBean.getRdfa(), PageContext.PAGE_SCOPE);
        } else {
            pageContext.setAttribute("error", "No se ha cargado ningún evento");
        }
    } catch (Exception e) {
        pageContext.setAttribute("error", "Error cargando el evento");
    }
%>