<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.util.CmsRequestUtil" %>

<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>

<div>
    <p>Su sesión ha expirado. Por favor, <a
            href="<cms:link>/proveedores-publica/acceso-proveedores/index.html</cms:link>?requestedResource=<%=CmsRequestUtil.encodeParamsWithUri(CmsFlexController.getCmsObject(request).getRequestContext().getUri(), request)%>">vuelva
        a acceder a través del siguiente formulario</a></p>
</div>