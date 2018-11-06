<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="java.util.List" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%
    String type = request.getParameter("type");
    String path = request.getParameter("path");
    CmsObject cmso = CmsFlexController.getCmsObject(request);
    CmsResourceFilter filter = CmsResourceFilter.ALL.addRequireFile();
    out.print("<div>");
    if (!StringUtils.isEmpty(type)) {
        filter = filter.addRequireType(
                OpenCms.getResourceManager().getResourceType(type));
        List<CmsResource> resources = cmso.readResources(path != null ? path : "/", filter, true);
        out.print("<ul>");
        for (int i = 0; i < resources.size(); i++) {
            CmsResource res = resources.get(i);
            out.print("<li>" + i + " - " + res.getRootPath() + "</li>");
        }
        out.print("</ul>");
    } else {
        out.print("<h2>" + "Debe incluir el parametro 'type'" + "</h2>");
    }
    out.print("</div>");
%>