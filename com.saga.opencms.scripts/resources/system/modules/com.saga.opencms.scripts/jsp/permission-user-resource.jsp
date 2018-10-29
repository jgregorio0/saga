<%@ page import="org.opencms.jsp.util.CmsJspStandardContextBean" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsUser" %>
<%@ page import="org.opencms.security.CmsPermissionSet" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<%
	CmsObject cmso = CmsJspStandardContextBean.getInstance(request).getVfs().getCmsObject();
	String resourceSitePath = "/es/transparencia/.content/sgindicadortransparencia/sgindicadortransparencia-00002.xml";
	CmsResource resource = cmso.readResource(resourceSitePath);

	CmsUser currentUser = cmso.getRequestContext().getCurrentUser();

	CmsPermissionSet permissionsForUser = cmso.getPermissions(resourceSitePath);
%>
<p>resource: <%=resource%></p>
<p>user: <%=currentUser%></p>
<p>permissions: <%=permissionsForUser%></p>