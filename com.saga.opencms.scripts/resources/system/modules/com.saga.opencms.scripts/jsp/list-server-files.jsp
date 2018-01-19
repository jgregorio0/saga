<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Arrays" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cms" uri="http://www.opencms.org/taglib/cms" %>
<%!
	String childPath(PageContext pageContext) {
		String path = (String)pageContext.getAttribute("path");
		String child = (String)pageContext.getAttribute("child");
		return CmsStringUtil.joinPaths(Arrays.asList(new String[]{path, "/", child}));
	}

	String parentPath(PageContext pageContext) {
		String path = (String)pageContext.getAttribute("path");
		return CmsResource.getParentFolder(path);
	}
%>

<div>
	<%
		String path = "/";
		String logsPath = OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebInf("logs/");
		String libsPath = OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebInf("libs/");
		String[] children = {};
		try {
			path = request.getParameter("path");
			path = path == null ? "/" : path;
			File f = new File(path);
			children = f.list();
			Arrays.sort(children);
		} catch (Exception e) {
			out.println(CmsException.getStackTraceAsString(e));
		} finally {
			pageContext.setAttribute("path", path);
			pageContext.setAttribute("children", children);
		}
	%>
	<ul>
		<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=/'>/</a></li>
		<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=logsPath%>'>LOGS</a></li>
		<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=libsPath%>'>LIBS</a></li>
		<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=parentPath(pageContext)%>'>..</a></li>
		<c:forEach items="${children}" var="child">
			<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=childPath(pageContext)%>'>${child}</a></li>
		</c:forEach>
	</ul>
</div>