<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>

<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="org.opencms.file.CmsResourceFilter" %>
<%@ page import="org.opencms.file.types.I_CmsResourceType" %>
<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="java.util.List" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div>
<%
	CmsObject cmso = CmsFlexController.getCmsObject(request);
	I_CmsResourceType containerPageType = OpenCms.getResourceManager().getResourceType("containerpage");
	I_CmsResourceType binaryType = OpenCms.getResourceManager().getResourceType("binary");
	I_CmsResourceType imageType = OpenCms.getResourceManager().getResourceType("image");
	I_CmsResourceType configType = OpenCms.getResourceManager().getResourceType("sitemap_config");
	CmsResourceFilter filter = CmsResourceFilter.DEFAULT_FILES;
	filter = filter.addExcludeType(containerPageType);
	filter = filter.addExcludeType(binaryType);
	filter = filter.addExcludeType(imageType);
	filter = filter.addExcludeType(configType);
	List<CmsResource> resources = cmso.readResources("/ca/.content/", filter, true);
	int i = 0;
	for(CmsResource r : resources){
		if (i == 20) {
			break;
		}
%>
<br/>recurso <%=r.getRootPath()%>
<%
		i++;
	}
%>
</div>