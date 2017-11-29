<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page buffer="none" session="false" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsProject" %>
<%@ page import="org.opencms.jsp.CmsJspBean" %>

<%
  CmsJspBean jspBean = new CmsJspBean();
  jspBean.init(pageContext, request, response);

  CmsObject cmso = jspBean.getCmsObject();
//  CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
  String projectName = "Offline";
  CmsProject offProject = cmso.readProject(projectName);
  cmso.getRequestContext().setCurrentProject(offProject);

  response.sendRedirect("/");
%>