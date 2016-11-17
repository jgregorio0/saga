<%@ page import="org.opencms.main.OpenCms" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.module.CmsModule" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<div>
	<%
		String error = null;
		String modulePath = "/system/modules/com.saga.opencms.fileupload/";
		CmsObject cmso = CmsFlexController.getCmsObject(request);
		try {
//      CmsResource resource = cmso.readResource(modulePath);
			OpenCms.getPublishManager().publishResource(cmso, modulePath);
		} catch (Exception e) {
			e.printStackTrace();
			error = e.getMessage();
		}

		if (error != null) {
			out.print("ERROR: " + error);
		} else {
			out.print("Publicado correctamente");
		}
	%>
</div>