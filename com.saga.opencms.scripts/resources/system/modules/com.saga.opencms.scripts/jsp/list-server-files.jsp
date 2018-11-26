<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.main.CmsException" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.util.CmsStringUtil" %>
<%@ page import="java.io.File" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.io.BufferedInputStream" %>
<%@ page import="java.io.FileInputStream" %>
<%@page buffer="none" session="false"  %>

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

<%
	String path = "/";
	String logsPath = OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebInf("logs/");
	String libsPath = OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebInf("lib/");
	String ocmsPath = OpenCms.getSystemInfo().getAbsoluteRfsPathRelativeToWebInf("");
	boolean isDirectory = true;
	String[] children = {};
	try {
		path = request.getParameter("path");
		path = path == null ? "/" : path;
		File f = new File(path);
		isDirectory = f.isDirectory();
		if (isDirectory) {
			children = f.list();
			Arrays.sort(children);
		} else {
			String mimeType = "application/zip";
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition","attachment; filename=\"" + f.getName() + "\"");

			byte[] buf = new byte[1024];
			long length = f.length();
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(f));
			ServletOutputStream os = response.getOutputStream();
			response.setContentLength((int) length);
			while ((in != null) && ((length = in.read(buf)) != -1)) {
				os.write(buf, 0, (int) length);
			}
			in.close();
			os.close();
		}
	} catch (Exception e) {
		out.println(CmsException.getStackTraceAsString(e));
	} finally {
		pageContext.setAttribute("path", path);
		pageContext.setAttribute("children", children);
	}
%>
<c:if test="<%=isDirectory%>">
	<div>
		<ul>
			<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=/'>/</a></li>
			<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=logsPath%>'>LOG</a></li>
			<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=libsPath%>'>LIB</a></li>
			<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=ocmsPath%>'>OCMS</a></li>
			<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=parentPath(pageContext)%>'>..</a></li>
			<c:forEach items="${children}" var="child">
				<li><a href='<cms:link>${cms.template.uri}</cms:link>?path=<%=childPath(pageContext)%>'>${child}</a></li>
			</c:forEach>
		</ul>
	</div>
</c:if>