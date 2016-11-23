<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="java.util.List" %>
<%@ page import="org.opencms.file.CmsResource" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="org.opencms.util.CmsUUID" %>
<%@ taglib prefix="c" uri="http://www.opencms.org/taglib/cms" %>
<%@page buffer="none" session="false" trimDirectiveWhitespaces="true"%>
<!DOCTYPE html>
<html lang="es">
<head>
	<title>Ejecucion Script Groovy</title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="SAGA SOLUCIONES - OpenCms Partners">

	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">

	<!-- Latest compiled and minified JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="http://code.jquery.com/jquery.min.js"></script>

</head>

<body>
<div class="container-fluid">
	<div class="row clearfix">
		<div class="col-md-12 column">
			<%
				String uuid = "16f31120-22a5-11de-afc0-0016e64f3b7f";
				CmsObject cmso = CmsFlexController.getCmsObject(request);
				CmsResource res = cmso.readResource(CmsUUID.valueOf(uuid));

			%>
			Recurso: <%=res.getRootPath()%>
		</div>
	</div>
</div>
</body>
</html>