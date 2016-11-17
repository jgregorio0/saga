<%@ page import="org.opencms.flex.CmsFlexController" %>
<%@ page import="org.opencms.file.CmsObject" %>
<%@ page import="org.opencms.file.CmsFile" %>
<%@ page import="org.opencms.file.CmsResource" %>
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
				CmsObject cmso = CmsFlexController.getCmsObject(request);
				CmsFile file = cmso.readFile("/.migration/old/serveis/il/acollida/acogida1213.html");

			%>
			<%=file.getName()%>
		</div>
	</div>
</div>
</body>
</html>